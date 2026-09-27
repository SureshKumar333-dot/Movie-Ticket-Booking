package com.cineverse.repository;

import com.cineverse.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByCustomerId(String customerId);
    List<Booking> findByCustomerIdAndStatus(String customerId, String status);
    List<Booking> findByShowIdAndStatusNot(String showId, String status);
    List<Booking> findByMovieIdAndTheatreIdAndHallIdAndShowDateAndShowTimeAndStatusNot(
            String movieId, String theatreId, String hallId, LocalDate showDate, String showTime, String status
    );
    List<Booking> findByStatusNot(String status);
}

