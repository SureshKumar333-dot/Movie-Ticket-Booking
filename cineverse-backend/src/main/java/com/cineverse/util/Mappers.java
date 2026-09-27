package com.cineverse.util;

import com.cineverse.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Mappers {

    private static final ObjectMapper OM = new ObjectMapper();

    private Mappers() {}

    public static Map<String, Object> movieToMap(Movie m) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", m.getId());
        map.put("title", m.getTitle());
        map.put("language", m.getLanguage());
        map.put("genre", m.getGenre());
        map.put("duration", m.getDuration());
        map.put("rating", m.getRating());
        map.put("badge", m.getBadge());
        map.put("description", m.getDescription());
        map.put("cast", m.getCastMembers());
        map.put("director", m.getDirector());
        map.put("poster", m.getPoster());
        map.put("price", Map.of(
                "standard", m.getPriceStandard() != null ? m.getPriceStandard() : 150,
                "premium", m.getPricePremium() != null ? m.getPricePremium() : 250
        ));
        return map;
    }

    public static Map<String, Object> theatreToMap(Theatre t, List<Hall> halls) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("name", t.getName());
        map.put("city", t.getCity());
        map.put("location", t.getLocation());
        map.put("phone", t.getPhone());
        map.put("halls", halls.stream().map(Hall::getId).toList());
        return map;
    }

    public static Map<String, Object> hallToMap(Hall h) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", h.getId());
        map.put("name", h.getName());
        map.put("theatreId", h.getTheatreId());
        map.put("totalSeats", h.getTotalSeats());
        map.put("features", JsonUtil.toStringList(h.getFeatures()));
        map.put("rows", JsonUtil.toStringList(h.getRows()));
        map.put("seatsPerRow", h.getSeatsPerRow());
        return map;
    }

    public static Map<String, Object> showToMap(Show s) {
        return showToMap(s, List.of());
    }

    public static Map<String, Object> showToMap(Show s, List<String> bookedSeats) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("movieId", s.getMovieId());
        map.put("theatreId", s.getTheatreId());
        map.put("hallId", s.getHallId());
        map.put("cityId", s.getCityId());
        map.put("showDate", s.getShowDate() != null ? s.getShowDate().toString() : null);
        map.put("showTime", s.getShowTime());
        map.put("time", s.getShowTime());
        int bookedCount = bookedSeats != null ? bookedSeats.size() : 0;
        int avail = s.getAvailableSeats() != null ? Math.max(0, s.getAvailableSeats() - bookedCount) : Math.max(0, 100 - bookedCount);
        map.put("availableSeats", avail);
        map.put("language", s.getLanguage());
        map.put("format", s.getFormat());
        map.put("bookedSeats", bookedSeats != null ? bookedSeats : List.of());
        return map;
    }

    public static Map<String, Object> bookingToMap(Booking b) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", b.getId());
        map.put("customerId", b.getCustomerId());
        map.put("showId", b.getShowId());
        map.put("movieId", b.getMovieId());
        map.put("theatreId", b.getTheatreId());
        map.put("hallId", b.getHallId());
        map.put("movieTitle", b.getMovieTitle());
        map.put("theatreName", b.getTheatreName());
        map.put("hallName", b.getHallName());
        map.put("cityId", b.getCityId());
        map.put("showDate", b.getShowDate() != null ? b.getShowDate().toString() : null);
        map.put("showTime", b.getShowTime());
        map.put("seats", JsonUtil.toStringList(b.getSeats()));
        map.put("seatType", b.getSeatType());
        map.put("ticketAmount", b.getTicketAmount());
        map.put("convenienceFee", b.getConvenienceFee());
        map.put("totalAmount", b.getTotalAmount());
        map.put("paymentMethod", b.getPaymentMethod());
        map.put("paymentStatus", b.getPaymentStatus());
        map.put("status", b.getStatus());
        map.put("bookedOn", b.getBookedOn() != null ? b.getBookedOn().toString() : null);
        map.put("updatedAt", b.getUpdatedAt() != null ? b.getUpdatedAt().toString() : null);
        return map;
    }

    public static Map<String, Object> cancellationToMap(Cancellation c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("bookingId", c.getBookingId());
        map.put("customerId", c.getCustomerId());
        map.put("movieTitle", c.getMovieTitle());
        map.put("theatreName", c.getTheatreName());
        map.put("showDate", c.getShowDate() != null ? c.getShowDate().toString() : null);
        map.put("showTime", c.getShowTime());
        map.put("seats", JsonUtil.toStringList(c.getSeats()));
        map.put("totalAmount", c.getTotalAmount());
        map.put("refundAmount", c.getRefundAmount());
        map.put("refundStatus", c.getRefundStatus());
        map.put("refundMethod", c.getRefundMethod());
        map.put("reason", c.getReason());
        map.put("cancelledAt", c.getCancelledAt() != null ? c.getCancelledAt().toString() : null);
        return map;
    }

    public static Map<String, Object> changeToMap(BookingChange c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("bookingId", c.getBookingId());
        map.put("customerId", c.getCustomerId());
        map.put("changeType", c.getChangeType());
        map.put("description", c.getDescription());
        map.put("before", JsonUtil.toMap(c.getBeforeState()));
        map.put("after", JsonUtil.toMap(c.getAfterState()));
        map.put("feePaid", c.getFeePaid());
        map.put("changedAt", c.getChangedAt() != null ? c.getChangedAt().toString() : null);
        return map;
    }

    public static String listToJsonArray(List<String> items) {
        ArrayNode arr = OM.createArrayNode();
        if (items != null) items.forEach(arr::add);
        return arr.toString();
    }
}
