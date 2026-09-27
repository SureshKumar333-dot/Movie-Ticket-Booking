package com.cineverse.service;

import com.cineverse.exception.BadRequestException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Booking;
import com.cineverse.model.Cancellation;
import com.cineverse.model.User;
import com.cineverse.repository.BookingRepository;
import com.cineverse.repository.CancellationRepository;
import com.cineverse.util.Mappers;
import com.cineverse.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class CancellationService {

    private final CancellationRepository cancellationRepository;
    private final BookingRepository bookingRepository;

    public CancellationService(CancellationRepository cancellationRepository, BookingRepository bookingRepository) {
        this.cancellationRepository = cancellationRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Map<String, Object>> findAll(String customerId, String bookingId) {
        User current = SecurityUtil.currentUserOptional();
        List<Cancellation> rows;

        if (bookingId != null && !bookingId.isBlank()) {
            rows = cancellationRepository.findByBookingId(bookingId);
        } else if (customerId != null && !customerId.isBlank()) {
            rows = cancellationRepository.findByCustomerId(customerId);
        } else if (current != null && SecurityUtil.isAdmin(current)) {
            rows = cancellationRepository.findAll();
        } else if (current != null) {
            rows = cancellationRepository.findByCustomerId(current.getId());
        } else {
            rows = cancellationRepository.findAll();
        }

        return rows.stream()
                .sorted(Comparator.comparing(Cancellation::getCancelledAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Mappers::cancellationToMap)
                .toList();
    }

    public Map<String, Object> findById(String id) {
        Cancellation c = cancellationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancellation not found"));
        return Mappers.cancellationToMap(c);
    }

    @Transactional
    public Map<String, Object> cancelBooking(String bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        int refundAmount = (int) Math.round(booking.getTotalAmount() * 0.9);

        Cancellation cancellation = new Cancellation();
        cancellation.setId("CAN" + System.currentTimeMillis());
        cancellation.setBookingId(bookingId);
        cancellation.setCustomerId(booking.getCustomerId());
        cancellation.setMovieTitle(booking.getMovieTitle());
        cancellation.setTheatreName(booking.getTheatreName());
        cancellation.setShowDate(booking.getShowDate());
        cancellation.setShowTime(booking.getShowTime());
        cancellation.setSeats(booking.getSeats());
        cancellation.setTotalAmount(booking.getTotalAmount());
        cancellation.setRefundAmount(refundAmount);
        cancellation.setRefundStatus("PENDING");
        cancellation.setRefundMethod(booking.getPaymentMethod());
        cancellation.setReason(reason != null && !reason.isBlank() ? reason : "No reason provided");
        cancellation.setCancelledAt(LocalDateTime.now());

        return Mappers.cancellationToMap(cancellationRepository.save(cancellation));
    }

    @Transactional
    public Map<String, Object> updateStatus(String id, Map<String, Object> body) {
        Cancellation cancellation = cancellationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancellation not found"));

        if (!SecurityUtil.isAdmin(SecurityUtil.currentUser())) {
            throw new BadRequestException("Only admins can update refund status");
        }
        if (body.containsKey("refundStatus")) {
            cancellation.setRefundStatus(String.valueOf(body.get("refundStatus")));
        }
        return Mappers.cancellationToMap(cancellationRepository.save(cancellation));
    }

    private void assertOwnerOrAdmin(String customerId) {
        User current = SecurityUtil.currentUser();
        if (!SecurityUtil.isAdmin(current) && !current.getId().equals(customerId)) {
            throw new BadRequestException("You can only access your own cancellations");
        }
    }
}
