package com.cineverse.repository;

import com.cineverse.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show, String> {

    @Query("""
        SELECT s FROM Show s
        WHERE (:movieId IS NULL OR s.movieId = :movieId)
          AND (:hallId IS NULL OR s.hallId = :hallId)
          AND (:theatreId IS NULL OR s.theatreId = :theatreId)
          AND (:showDate IS NULL OR s.showDate = :showDate)
        """)
    List<Show> search(
            @Param("movieId") String movieId,
            @Param("hallId") String hallId,
            @Param("theatreId") String theatreId,
            @Param("showDate") LocalDate showDate
    );

    List<Show> findByMovieId(String movieId);
}
