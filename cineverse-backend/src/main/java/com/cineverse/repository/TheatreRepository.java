package com.cineverse.repository;

import com.cineverse.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, String> {
    List<Theatre> findByCity(String city);
}
