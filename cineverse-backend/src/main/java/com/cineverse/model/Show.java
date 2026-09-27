package com.cineverse.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @Column(length = 10)
    private String id;

    @Column(name = "movie_id", nullable = false)
    private String movieId;

    @Column(name = "theatre_id", nullable = false)
    private String theatreId;

    @Column(name = "hall_id", nullable = false)
    private String hallId;

    @Column(name = "city_id", length = 5)
    private String cityId;

    private LocalDate showDate;

    private String showTime;

    private Integer availableSeats;

    private String language;

    private String format;

    public Show() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }

    public String getCityId() { return cityId; }
    public void setCityId(String cityId) { this.cityId = cityId; }

    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
