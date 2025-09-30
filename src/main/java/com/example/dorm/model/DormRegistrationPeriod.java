package com.example.dorm.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
public class DormRegistrationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    private Integer capacity;

    @Column(length = 2000)
    private String notes;

    @Enumerated(EnumType.STRING)
    private DormRegistrationPeriodStatus status = DormRegistrationPeriodStatus.SCHEDULED;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private Long submittedCount = 0L;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.startTime == null) {
            this.startTime = createdAt;
        }
        if (this.status == null) {
            this.status = DormRegistrationPeriodStatus.SCHEDULED;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DormRegistrationPeriodStatus getStatus() {
        return status;
    }

    public void setStatus(DormRegistrationPeriodStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOpen() {
        return this.status == DormRegistrationPeriodStatus.OPEN;
    }

    public boolean isExpired() {
        return this.endTime != null && this.endTime.isBefore(LocalDateTime.now());
    }

    public Long getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(Long submittedCount) {
        this.submittedCount = submittedCount;
    }
}
