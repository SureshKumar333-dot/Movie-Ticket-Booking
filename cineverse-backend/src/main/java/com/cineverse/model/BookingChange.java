package com.cineverse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_changes")
public class BookingChange {

    @Id
    @Column(length = 20)
    private String id;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    private String changeType;
    private String description;

    @Column(columnDefinition = "JSON")
    private String beforeState;

    @Column(columnDefinition = "JSON")
    private String afterState;

    private Integer feePaid;

    private LocalDateTime changedAt;

    public BookingChange() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBeforeState() { return beforeState; }
    public void setBeforeState(String beforeState) { this.beforeState = beforeState; }

    public String getAfterState() { return afterState; }
    public void setAfterState(String afterState) { this.afterState = afterState; }

    public Integer getFeePaid() { return feePaid; }
    public void setFeePaid(Integer feePaid) { this.feePaid = feePaid; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
