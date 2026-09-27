package com.cineverse.service;

import com.cineverse.exception.BadRequestException;
import com.cineverse.exception.ConflictException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Booking;
import com.cineverse.model.User;
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
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Map<String, Object>> findAll(String customerId, String status) {
        User current = SecurityUtil.currentUserOptional();
        List<Booking> bookings;

        if (current != null && SecurityUtil.isAdmin(current)) {
            bookings = (customerId != null && !customerId.isBlank())
                    ? bookingRepository.findByCustomerId(customerId)
                    : bookingRepository.findAll();
        } else if (customerId != null && !customerId.isBlank()) {
            bookings = bookingRepository.findByCustomerId(customerId);
        } else if (current != null) {
            bookings = bookingRepository.findByCustomerId(current.getId());
        } else {
            bookings = bookingRepository.findAll();
        }

        if (status != null && !status.isBlank()) {
            bookings = bookings.stream().filter(b -> status.equals(b.getStatus())).toList();
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getBookedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Mappers::bookingToMap)
                .toList();
    }

    public Map<String, Object> findById(String id) {
        Booking booking = getBookingOrThrow(id);
        return Mappers.bookingToMap(booking);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> create(Map<String, Object> body) {
        User current = SecurityUtil.currentUserOptional();

        if (body.get("customerId") == null || body.get("showId") == null || body.get("movieId") == null
                || body.get("seats") == null || body.get("totalAmount") == null || body.get("paymentMethod") == null) {
            throw new BadRequestException("Missing required fields");
        }

        List<String> seats = (List<String>) body.get("seats");
        if (seats.isEmpty()) throw new BadRequestException("At least one seat required");
        if (seats.size() > 8) throw new BadRequestException("Max 8 seats per booking");

        String customerId = String.valueOf(body.get("customerId"));
        if (current != null && !current.getId().equals(customerId) && !SecurityUtil.isAdmin(current)) {
            throw new BadRequestException("You can only book for yourself");
        }

        String showId = String.valueOf(body.get("showId"));
        String movieId = String.valueOf(body.get("movieId"));
        String theatreId = stringOrEmpty(body.get("theatreId"));
        String hallId = stringOrEmpty(body.get("hallId"));
        LocalDate showDate = body.get("showDate") != null ? LocalDate.parse(String.valueOf(body.get("showDate"))) : null;
        String showTime = stringOrEmpty(body.get("showTime"));

        // Validate that requested seats are not already booked
        validateSeatsAvailability(showId, movieId, theatreId, hallId, showDate, showTime, seats, null);

        Booking booking = new Booking();
        booking.setId("B" + String.valueOf(System.currentTimeMillis()).substring(6));
        booking.setCustomerId(customerId);
        booking.setShowId(showId);
        booking.setMovieId(movieId);
        booking.setTheatreId(theatreId);
        booking.setHallId(hallId);
        booking.setCityId(stringOrEmpty(body.get("cityId")));
        booking.setMovieTitle(stringOrEmpty(body.get("movieTitle")));
        booking.setTheatreName(stringOrEmpty(body.get("theatreName")));
        booking.setHallName(stringOrEmpty(body.get("hallName")));
        if (showDate != null) {
            booking.setShowDate(showDate);
        }
        booking.setShowTime(showTime);
        booking.setSeats(JsonUtil.toJson(seats));
        booking.setSeatType(stringOrDefault(body.get("seatType"), "Standard"));
        booking.setTicketAmount(intOrZero(body.get("ticketAmount")));
        booking.setConvenienceFee(intOrZero(body.get("convenienceFee")));
        booking.setTotalAmount(((Number) body.get("totalAmount")).intValue());
        booking.setPaymentMethod(String.valueOf(body.get("paymentMethod")));
        booking.setPaymentStatus("SUCCESS");
        booking.setStatus("CONFIRMED");
        booking.setBookedOn(LocalDateTime.now());

        return Mappers.bookingToMap(bookingRepository.save(booking));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> update(String id, Map<String, Object> body) {
        Booking booking = getBookingOrThrow(id);
        assertOwnerOrAdmin(booking);

        if ("CANCELLED".equals(body.get("status")) && "CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Booking is already cancelled");
        }

        if (body.containsKey("seats")) {
            List<String> newSeats = (List<String>) body.get("seats");
            String showId = body.containsKey("showId") ? String.valueOf(body.get("showId")) : booking.getShowId();
            LocalDate showDate = body.containsKey("showDate") ? LocalDate.parse(String.valueOf(body.get("showDate"))) : booking.getShowDate();
            String showTime = body.containsKey("showTime") ? String.valueOf(body.get("showTime")) : booking.getShowTime();
            validateSeatsAvailability(showId, booking.getMovieId(), booking.getTheatreId(), booking.getHallId(), showDate, showTime, newSeats, id);
            booking.setSeats(JsonUtil.toJson(newSeats));
        }

        if (body.containsKey("status")) booking.setStatus(String.valueOf(body.get("status")));
        if (body.containsKey("showDate")) booking.setShowDate(LocalDate.parse(String.valueOf(body.get("showDate"))));
        if (body.containsKey("showTime")) booking.setShowTime(String.valueOf(body.get("showTime")));
        if (body.containsKey("seatType")) booking.setSeatType(String.valueOf(body.get("seatType")));
        if (body.containsKey("showId")) booking.setShowId(String.valueOf(body.get("showId")));
        booking.setUpdatedAt(LocalDateTime.now());

        return Mappers.bookingToMap(bookingRepository.save(booking));
    }

    @Transactional
    public void delete(String id) {
        Booking booking = getBookingOrThrow(id);
        if (!SecurityUtil.isAdmin(currentUser())) {
            assertOwnerOrAdmin(booking);
        }
        bookingRepository.deleteById(id);
    }

    private void validateSeatsAvailability(String showId, String movieId, String theatreId, String hallId,
                                           LocalDate showDate, String showTime, List<String> requestedSeats, String excludeBookingId) {
        List<Booking> activeBookings = bookingRepository.findByStatusNot("CANCELLED");
        Set<String> alreadyBooked = new HashSet<>();

        for (Booking b : activeBookings) {
            if (excludeBookingId != null && excludeBookingId.equals(b.getId())) {
                continue;
            }
            boolean matchesShowId = b.getShowId() != null && b.getShowId().equals(showId);
            boolean matchesSlot = b.getMovieId() != null && b.getMovieId().equals(movieId)
                    && b.getTheatreId() != null && b.getTheatreId().equals(theatreId)
                    && b.getHallId() != null && b.getHallId().equals(hallId)
                    && (showDate == null || (b.getShowDate() != null && b.getShowDate().equals(showDate)))
                    && (showTime.isBlank() || (b.getShowTime() != null && b.getShowTime().equalsIgnoreCase(showTime)));

            if (matchesShowId || matchesSlot) {
                alreadyBooked.addAll(JsonUtil.toStringList(b.getSeats()));
            }
        }

        List<String> conflicts = requestedSeats.stream().filter(alreadyBooked::contains).toList();
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Seat(s) " + String.join(", ", conflicts)
                    + " have already been booked for this show. Please select different seats.");
        }
    }

    private Booking getBookingOrThrow(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private void assertOwnerOrAdmin(Booking booking) {
        User current = SecurityUtil.currentUser();
        if (!SecurityUtil.isAdmin(current) && !current.getId().equals(booking.getCustomerId())) {
            throw new BadRequestException("You can only access your own bookings");
        }
    }

    private User currentUser() {
        return SecurityUtil.currentUser();
    }

    private String stringOrEmpty(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    private String stringOrDefault(Object value, String defaultValue) {
        return value != null && !String.valueOf(value).isBlank() ? String.valueOf(value) : defaultValue;
    }

    private int intOrZero(Object value) {
        return value instanceof Number n ? n.intValue() : 0;
    }
}
