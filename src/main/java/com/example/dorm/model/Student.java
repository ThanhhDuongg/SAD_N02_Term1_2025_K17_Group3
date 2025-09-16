package com.example.dorm.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Access(AccessType.FIELD)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String name;
    private LocalDate dob;
    private String gender;
    private String phone;
    private String address;
    private String email;
    private String department;

    @NotNull(message = "Năm học không được để trống")
    @Min(1)
    @Max(6)
    @Column(name = "study_year")
    private Integer studyYear;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // NEW: Link to user account
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters (keep existing ones and add new ones)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ... rest of existing getters and setters remain the same
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Integer getStudyYear() { return studyYear; }
    public void setStudyYear(Integer studyYear) { this.studyYear = studyYear; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
