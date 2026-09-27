package com.cineverse.repository;

import com.cineverse.model.BookingChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingChangeRepository extends JpaRepository<BookingChange, String> {
    List<BookingChange> findByCustomerId(String customerId);
    List<BookingChange> findByBookingId(String bookingId);
    List<BookingChange> findByChangeType(String changeType);
}
