package com.example.dorm.service;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.repository.MaintenanceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceRequestService {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        normalizeRequest(request);
        return maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequest> getRequestsByStudent(Long studentId) {
        return maintenanceRequestRepository.findByStudent_Id(studentId).stream()
                .sorted(java.util.Comparator.comparing(MaintenanceRequest::getCreatedAt).reversed())
                .toList();
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return getAllRequests();
        }
        return maintenanceRequestRepository.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase());
    }

    public Optional<MaintenanceRequest> getRequest(Long id) {
        return maintenanceRequestRepository.findById(id);
    }

    public MaintenanceRequest updateStatus(Long id, String status) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance request not found"));
        if (status != null) {
            request.setStatus(status.trim().toUpperCase());
        }
        return maintenanceRequestRepository.save(request);
    }

    public long countAllRequests() {
        return maintenanceRequestRepository.count();
    }

    public long countRequestsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return countAllRequests();
        }
        return maintenanceRequestRepository.countByStatus(status.trim().toUpperCase());
    }

    private void normalizeRequest(MaintenanceRequest request) {
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("PENDING");
        } else {
            request.setStatus(request.getStatus().trim().toUpperCase());
        }
        if (request.getRequestType() != null) {
            request.setRequestType(request.getRequestType().trim().toUpperCase());
        }
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }
    }
}
