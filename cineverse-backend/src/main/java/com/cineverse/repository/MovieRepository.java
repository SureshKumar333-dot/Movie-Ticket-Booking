package com.cineverse.repository;

import com.cineverse.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByTitleIgnoreCase(String title);
}
