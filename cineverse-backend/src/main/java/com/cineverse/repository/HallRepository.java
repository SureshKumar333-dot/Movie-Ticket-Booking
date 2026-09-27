package com.cineverse.repository;

import com.cineverse.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, String> {
    List<Hall> findByTheatreId(String theatreId);
}
