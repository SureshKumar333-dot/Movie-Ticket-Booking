package com.cineverse.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(length = 10)
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "show_id", nullable = false)
    private String showId;

    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "theatre_id")
    private String theatreId;

    @Column(name = "hall_id")
    private String hallId;

    private String movieTitle;
    private String theatreName;
    private String hallName;

    @Column(name = "city_id", length = 5)
    private String cityId;

    private LocalDate showDate;
    private String showTime;

    @Column(columnDefinition = "JSON")
    private String seats;

    private String seatType;
    private Integer ticketAmount;
    private Integer convenienceFee;
    private Integer totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String status;

    private LocalDateTime bookedOn;
    private LocalDateTime updatedAt;

    public Booking() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getShowId() { return showId; }
    public void setShowId(String showId) { this.showId = showId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public String getCityId() { return cityId; }
    public void setCityId(String cityId) { this.cityId = cityId; }

    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public Integer getTicketAmount() { return ticketAmount; }
    public void setTicketAmount(Integer ticketAmount) { this.ticketAmount = ticketAmount; }

    public Integer getConvenienceFee() { return convenienceFee; }
    public void setConvenienceFee(Integer convenienceFee) { this.convenienceFee = convenienceFee; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookedOn() { return bookedOn; }
    public void setBookedOn(LocalDateTime bookedOn) { this.bookedOn = bookedOn; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
