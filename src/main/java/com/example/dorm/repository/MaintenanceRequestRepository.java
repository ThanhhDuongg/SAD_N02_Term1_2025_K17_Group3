package com.example.dorm.repository;

import com.example.dorm.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStudent_Id(Long studentId);

    List<MaintenanceRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<MaintenanceRequest> findAllByOrderByCreatedAtDesc();

    List<MaintenanceRequest> findAllByOrderByUpdatedAtDesc();

    List<MaintenanceRequest> findByStatusIgnoreCaseOrderByUpdatedAtDesc(String status);

    List<MaintenanceRequest> findByHandledBy_UsernameIgnoreCaseOrderByUpdatedAtDesc(String username);

    List<MaintenanceRequest> findByStatusIgnoreCaseAndHandledBy_UsernameIgnoreCaseOrderByUpdatedAtDesc(String status, String username);

    Optional<MaintenanceRequest> findByStudent_IdAndDescription(Long studentId, String description);

    long countByStatus(String status);

    List<MaintenanceRequest> findByStudent_IdOrderByUpdatedAtDesc(Long studentId);

    List<MaintenanceRequest> findByHandledBy_UsernameOrderByUpdatedAtDesc(String username);
}
