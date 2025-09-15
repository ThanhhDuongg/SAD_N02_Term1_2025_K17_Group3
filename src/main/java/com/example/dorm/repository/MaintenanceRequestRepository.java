package com.example.dorm.repository;

import com.example.dorm.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStudent_Id(Long studentId);

    List<MaintenanceRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<MaintenanceRequest> findAllByOrderByCreatedAtDesc();
}
