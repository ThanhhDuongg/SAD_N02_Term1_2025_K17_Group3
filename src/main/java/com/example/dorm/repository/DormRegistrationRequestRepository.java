package com.example.dorm.repository;

import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DormRegistrationRequestRepository extends JpaRepository<DormRegistrationRequest, Long> {
    List<DormRegistrationRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<DormRegistrationRequest> findAllByOrderByCreatedAtDesc();
    List<DormRegistrationRequest> findByStatusOrderByCreatedAtDesc(DormRegistrationStatus status);
}
