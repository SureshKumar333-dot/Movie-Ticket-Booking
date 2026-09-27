package com.cineverse.service;

import com.cineverse.exception.BadRequestException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Booking;
import com.cineverse.model.Show;
import com.cineverse.repository.BookingRepository;
import com.cineverse.repository.ShowRepository;
import com.cineverse.util.JsonUtil;
import com.cineverse.util.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;

    public ShowService(ShowRepository showRepository, BookingRepository bookingRepository) {
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Map<String, Object>> findAll(String movieId, String hallId, String theatreId, String date) {
        LocalDate showDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : null;
        List<Show> shows = showRepository.search(
                blankToNull(movieId),
                blankToNull(hallId),
                blankToNull(theatreId),
                showDate
        );

        List<Booking> activeBookings = bookingRepository.findByStatusNot("CANCELLED");

        if (shows.isEmpty() && movieId != null && !movieId.isBlank() && showDate != null) {
            List<Show> allMovieShows = showRepository.search(movieId, blankToNull(hallId), blankToNull(theatreId), null);
            Map<String, Show> distinctSlotMap = new java.util.LinkedHashMap<>();
            for (Show s : allMovieShows) {
                String key = s.getTheatreId() + "_" + s.getHallId() + "_" + s.getShowTime();
                distinctSlotMap.putIfAbsent(key, s);
            }
            return distinctSlotMap.values().stream()
                    .map(s -> {
                        List<String> booked = getBookedSeats(s, showDate, activeBookings);
                        Map<String, Object> map = Mappers.showToMap(s, booked);
                        map.put("showDate", showDate.toString());
                        return map;
                    })
                    .toList();
        }

        return shows.stream()
                .map(s -> {
                    LocalDate d = s.getShowDate() != null ? s.getShowDate() : showDate;
                    List<String> booked = getBookedSeats(s, d, activeBookings);
                    return Mappers.showToMap(s, booked);
                })
                .toList();
    }

    public Map<String, Object> findById(String id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
        List<Booking> activeBookings = bookingRepository.findByStatusNot("CANCELLED");
        List<String> booked = getBookedSeats(show, show.getShowDate(), activeBookings);
        return Mappers.showToMap(show, booked);
    }

    private List<String> getBookedSeats(Show show, LocalDate date, List<Booking> activeBookings) {
        List<String> booked = new ArrayList<>();
        if (show == null || activeBookings == null) return booked;

        for (Booking b : activeBookings) {
            boolean matchesShowId = b.getShowId() != null && b.getShowId().equals(show.getId());
            boolean matchesSlot = b.getMovieId() != null && b.getMovieId().equals(show.getMovieId())
                    && b.getTheatreId() != null && b.getTheatreId().equals(show.getTheatreId())
                    && b.getHallId() != null && b.getHallId().equals(show.getHallId())
                    && (date == null || (b.getShowDate() != null && b.getShowDate().equals(date)))
                    && (b.getShowTime() != null && b.getShowTime().equalsIgnoreCase(show.getShowTime()));

            if (matchesShowId || matchesSlot) {
                List<String> seats = JsonUtil.toStringList(b.getSeats());
                for (String seat : seats) {
                    if (!booked.contains(seat)) {
                        booked.add(seat);
                    }
                }
            }
        }
        return booked;
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body) {
        if (body.get("movieId") == null || body.get("hallId") == null || body.get("theatreId") == null
                || body.get("showDate") == null || body.get("showTime") == null) {
            throw new BadRequestException("Missing required show fields");
        }
        Show show = new Show();
        show.setId("S" + String.valueOf(System.currentTimeMillis()).substring(7));
        applyBody(show, body);
        if (show.getAvailableSeats() == null) show.setAvailableSeats(100);
        return Mappers.showToMap(showRepository.save(show));
    }

    @Transactional
    public Map<String, Object> update(String id, Map<String, Object> body) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
        applyBody(show, body);
        return Mappers.showToMap(showRepository.save(show));
    }

    @Transactional
    public void delete(String id) {
        if (!showRepository.existsById(id)) {
            throw new ResourceNotFoundException("Show not found");
        }
        showRepository.deleteById(id);
    }

    private void applyBody(Show show, Map<String, Object> body) {
        if (body.containsKey("movieId")) show.setMovieId(String.valueOf(body.get("movieId")));
        if (body.containsKey("theatreId")) show.setTheatreId(String.valueOf(body.get("theatreId")));
        if (body.containsKey("hallId")) show.setHallId(String.valueOf(body.get("hallId")));
        if (body.containsKey("cityId")) show.setCityId(String.valueOf(body.get("cityId")));
        if (body.containsKey("showDate")) show.setShowDate(LocalDate.parse(String.valueOf(body.get("showDate"))));
        if (body.containsKey("showTime")) show.setShowTime(String.valueOf(body.get("showTime")));
        if (body.containsKey("availableSeats")) show.setAvailableSeats(((Number) body.get("availableSeats")).intValue());
        if (body.containsKey("language")) show.setLanguage(String.valueOf(body.get("language")));
        if (body.containsKey("format")) show.setFormat(String.valueOf(body.get("format")));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
