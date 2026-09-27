package com.cineverse.repository;

import com.cineverse.model.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancellationRepository extends JpaRepository<Cancellation, String> {
    List<Cancellation> findByCustomerId(String customerId);
    List<Cancellation> findByBookingId(String bookingId);
}
