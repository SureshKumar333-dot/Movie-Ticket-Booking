package com.cineverse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "halls")
public class Hall {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "theatre_id", nullable = false)
    private String theatreId;

    private Integer totalSeats;

    @Column(columnDefinition = "JSON")
    private String features;

    @Column(name = "hall_rows", columnDefinition = "JSON")
    private String rows;

    private Integer seatsPerRow;

    public Hall() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getRows() { return rows; }
    public void setRows(String rows) { this.rows = rows; }

    public Integer getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(Integer seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}
