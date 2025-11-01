package com.example.dorm.repository;

import com.example.dorm.model.StudentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Long> {

    List<StudentNotification> findByStudent_IdAndReadFalseOrderByCreatedAtAsc(Long studentId);
}
