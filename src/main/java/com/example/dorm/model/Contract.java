package com.example.dorm.model;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Room room;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    @Enumerated(EnumType.STRING)
    private PaymentPlan paymentPlan = PaymentPlan.MONTHLY;

    /**
     * Preferred day within a month to generate rent invoices. If null the system
     * will fall back to the contract start day.
     */
    private Integer billingDayOfMonth;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public PaymentPlan getPaymentPlan() { return paymentPlan; }
    public void setPaymentPlan(PaymentPlan paymentPlan) { this.paymentPlan = paymentPlan; }
    public Integer getBillingDayOfMonth() { return billingDayOfMonth; }
    public void setBillingDayOfMonth(Integer billingDayOfMonth) { this.billingDayOfMonth = billingDayOfMonth; }
}