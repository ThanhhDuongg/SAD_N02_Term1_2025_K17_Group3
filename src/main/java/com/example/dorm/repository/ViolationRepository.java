package com.example.dorm.repository;

import com.example.dorm.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findByStudent_Id(Long studentId);
    List<Violation> findByRoom_Id(Long roomId);
}
