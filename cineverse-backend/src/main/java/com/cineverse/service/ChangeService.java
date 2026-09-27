package com.cineverse.service;

import com.cineverse.exception.BadRequestException;
import com.cineverse.exception.ConflictException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Booking;
import com.cineverse.model.BookingChange;
import com.cineverse.model.User;
import com.cineverse.repository.BookingChangeRepository;
import com.cineverse.repository.BookingRepository;
import com.cineverse.util.JsonUtil;
import com.cineverse.util.Mappers;
import com.cineverse.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChangeService {

    private static final int CHANGE_FEE = 50;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "SEAT_CHANGE", "DATE_CHANGE", "SHOW_CHANGE", "SEAT_TYPE_CHANGE"
    );

    private final BookingChangeRepository changeRepository;
    private final BookingRepository bookingRepository;

    public ChangeService(BookingChangeRepository changeRepository, BookingRepository bookingRepository) {
        this.changeRepository = changeRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Map<String, Object>> findAll(String customerId, String bookingId, String changeType) {
        User current = SecurityUtil.currentUserOptional();
        List<BookingChange> rows;

        if (bookingId != null && !bookingId.isBlank()) {
            rows = changeRepository.findByBookingId(bookingId);
        } else if (customerId != null && !customerId.isBlank()) {
            rows = changeRepository.findByCustomerId(customerId);
        } else if (current != null && SecurityUtil.isAdmin(current)) {
            rows = changeRepository.findAll();
        } else if (current != null) {
            rows = changeRepository.findByCustomerId(current.getId());
        } else {
            rows = changeRepository.findAll();
        }

        if (changeType != null && !changeType.isBlank()) {
            rows = rows.stream().filter(c -> changeType.equals(c.getChangeType())).toList();
        }

        return rows.stream()
                .sorted(Comparator.comparing(BookingChange::getChangedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Mappers::changeToMap)
                .toList();
    }

    public Map<String, Object> findById(String id) {
        BookingChange change = changeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Change record not found"));
        return Mappers.changeToMap(change);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> applyChange(String bookingId, Map<String, Object> updates, String changeType, String description) {
        if (updates == null || changeType == null || !ALLOWED_TYPES.contains(changeType)) {
            throw new BadRequestException("bookingId, updates, and valid changeType are required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Cannot change a cancelled booking");
        }

        List<String> targetSeats = updates.containsKey("seats")
                ? (List<String>) updates.get("seats")
                : JsonUtil.toStringList(booking.getSeats());
        String targetShowId = updates.containsKey("showId")
                ? String.valueOf(updates.get("showId"))
                : booking.getShowId();
        LocalDate targetShowDate = updates.containsKey("showDate")
                ? LocalDate.parse(String.valueOf(updates.get("showDate")))
                : booking.getShowDate();
        String targetShowTime = updates.containsKey("showTime")
                ? String.valueOf(updates.get("showTime"))
                : booking.getShowTime();

        // Validate seats availability for the target show / date / time
        List<Booking> activeBookings = bookingRepository.findByStatusNot("CANCELLED");
        Set<String> alreadyBooked = new HashSet<>();
        for (Booking b : activeBookings) {
            if (bookingId.equals(b.getId())) continue;
            boolean matchesShowId = b.getShowId() != null && b.getShowId().equals(targetShowId);
            boolean matchesSlot = b.getMovieId() != null && b.getMovieId().equals(booking.getMovieId())
                    && b.getTheatreId() != null && b.getTheatreId().equals(booking.getTheatreId())
                    && b.getHallId() != null && b.getHallId().equals(booking.getHallId())
                    && (targetShowDate == null || (b.getShowDate() != null && b.getShowDate().equals(targetShowDate)))
                    && (targetShowTime == null || targetShowTime.isBlank() || (b.getShowTime() != null && b.getShowTime().equalsIgnoreCase(targetShowTime)));

            if (matchesShowId || matchesSlot) {
                alreadyBooked.addAll(JsonUtil.toStringList(b.getSeats()));
            }
        }

        List<String> conflicts = targetSeats.stream().filter(alreadyBooked::contains).toList();
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Seat(s) " + String.join(", ", conflicts)
                    + " have already been booked for this show. Please choose different seats.");
        }

        Map<String, Object> before = new LinkedHashMap<>();
        before.put("seats", JsonUtil.toStringList(booking.getSeats()));
        before.put("showDate", booking.getShowDate() != null ? booking.getShowDate().toString() : null);
        before.put("showTime", booking.getShowTime());
        before.put("seatType", booking.getSeatType());
        before.put("showId", booking.getShowId());

        if (updates.containsKey("seats")) booking.setSeats(JsonUtil.toJson(updates.get("seats")));
        if (updates.containsKey("showDate")) booking.setShowDate(targetShowDate);
        if (updates.containsKey("showTime")) booking.setShowTime(targetShowTime);
        if (updates.containsKey("seatType")) booking.setSeatType(String.valueOf(updates.get("seatType")));
        if (updates.containsKey("showId")) booking.setShowId(targetShowId);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        BookingChange change = new BookingChange();
        change.setId("CHG" + System.currentTimeMillis());
        change.setBookingId(bookingId);
        change.setCustomerId(booking.getCustomerId());
        change.setChangeType(changeType);
        change.setDescription(description != null && !description.isBlank()
                ? description
                : changeType.replace('_', ' ') + " applied");
        change.setBeforeState(JsonUtil.toJson(before));
        change.setAfterState(JsonUtil.toJson(updates));
        change.setFeePaid(CHANGE_FEE);
        change.setChangedAt(LocalDateTime.now());
        changeRepository.save(change);

        Map<String, Object> result = Mappers.changeToMap(change);
        result.put("updatedBooking", Mappers.bookingToMap(booking));
        return result;
    }

    private void assertOwnerOrAdmin(String customerId) {
        User current = SecurityUtil.currentUser();
        if (!SecurityUtil.isAdmin(current) && !current.getId().equals(customerId)) {
            throw new BadRequestException("You can only access your own change records");
        }
    }
}
