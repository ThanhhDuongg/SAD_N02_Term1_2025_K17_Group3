package com.example.dorm.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class DormRegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id")
    private DormRegistrationPeriod period;

    @Column(length = 100)
    private String desiredRoomType;

    @Column(length = 50)
    private String preferredRoomNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expectedMoveInDate;

    @Column(length = 2000)
    private String additionalNotes;

    @Enumerated(EnumType.STRING)
    private DormRegistrationStatus status = DormRegistrationStatus.PENDING;

    @Column(length = 2000)
    private String adminNotes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate approvedStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate approvedEndDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) {
            this.status = DormRegistrationStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public DormRegistrationPeriod getPeriod() {
        return period;
    }

    public void setPeriod(DormRegistrationPeriod period) {
        this.period = period;
    }

    public String getDesiredRoomType() {
        return desiredRoomType;
    }

    public void setDesiredRoomType(String desiredRoomType) {
        this.desiredRoomType = desiredRoomType;
    }

    public String getPreferredRoomNumber() {
        return preferredRoomNumber;
    }

    public void setPreferredRoomNumber(String preferredRoomNumber) {
        this.preferredRoomNumber = preferredRoomNumber;
    }

    public LocalDate getExpectedMoveInDate() {
        return expectedMoveInDate;
    }

    public void setExpectedMoveInDate(LocalDate expectedMoveInDate) {
        this.expectedMoveInDate = expectedMoveInDate;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public DormRegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(DormRegistrationStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public LocalDate getApprovedStartDate() {
        return approvedStartDate;
    }

    public void setApprovedStartDate(LocalDate approvedStartDate) {
        this.approvedStartDate = approvedStartDate;
    }

    public LocalDate getApprovedEndDate() {
        return approvedEndDate;
    }

    public void setApprovedEndDate(LocalDate approvedEndDate) {
        this.approvedEndDate = approvedEndDate;
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
}
