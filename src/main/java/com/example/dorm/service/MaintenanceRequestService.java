package com.example.dorm.service;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.User;
import com.example.dorm.repository.MaintenanceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MaintenanceRequestService {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        normalizeRequest(request);
        return maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequest> getRequestsByStudent(Long studentId) {
        return maintenanceRequestRepository.findByStudent_IdOrderByUpdatedAtDesc(studentId);
    }

    public List<MaintenanceRequest> getAllRequests() {
        List<MaintenanceRequest> requests = maintenanceRequestRepository.findAllByOrderByUpdatedAtDesc();
        if (requests.isEmpty()) {
            return maintenanceRequestRepository.findAllByOrderByCreatedAtDesc();
        }
        return requests;
    }

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        return getRequests(status, null, null, null);
    }

    public List<MaintenanceRequest> getRequests(String status, String type, String keyword, String assignedUsername) {
        Stream<MaintenanceRequest> stream = getAllRequests().stream();

        if (status != null && !status.isBlank()) {
            String normalizedStatus = status.trim().toUpperCase();
            stream = stream.filter(request -> normalizedStatus.equalsIgnoreCase(request.getStatus()));
        }

        if (type != null && !type.isBlank()) {
            String normalizedType = type.trim().toUpperCase();
            stream = stream.filter(request -> normalizedType.equalsIgnoreCase(request.getRequestType()));
        }

        if (keyword != null && !keyword.isBlank()) {
            String normalizedKeyword = keyword.trim().toLowerCase();
            stream = stream.filter(request -> matchesKeyword(request, normalizedKeyword));
        }

        if (assignedUsername != null && !assignedUsername.isBlank()) {
            stream = stream.filter(request -> request.getHandledBy() != null
                    && assignedUsername.equalsIgnoreCase(request.getHandledBy().getUsername()));
        }

        return stream.sorted(Comparator.comparing(
                        MaintenanceRequest::getUpdatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed())
                .toList();
    }

    public Optional<MaintenanceRequest> getRequest(Long id) {
        return maintenanceRequestRepository.findById(id);
    }

    public MaintenanceRequest updateStatus(Long id, String status, String resolutionNotes, User handledBy) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance request not found"));
        if (status != null) {
            request.setStatus(status.trim().toUpperCase());
        }

        if (resolutionNotes != null) {
            String normalized = resolutionNotes.trim();
            request.setResolutionNotes(normalized.isEmpty() ? null : normalized);
        }

        if (handledBy != null) {
            request.setHandledBy(handledBy);
        } else if ("PENDING".equalsIgnoreCase(request.getStatus())) {
            request.setHandledBy(null);
        }

        request.setUpdatedAt(LocalDateTime.now());
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
        if (request.getUpdatedAt() == null) {
            request.setUpdatedAt(request.getCreatedAt());
        }
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            request.setResolutionNotes(request.getResolutionNotes().trim());
        } else {
            request.setResolutionNotes(null);
        }
        request.setHandledBy(null);
    }

    private boolean matchesKeyword(MaintenanceRequest request, String keyword) {
        if (request.getStudent() != null) {
            String name = request.getStudent().getName();
            String code = request.getStudent().getCode();
            if ((name != null && name.toLowerCase().contains(keyword))
                    || (code != null && code.toLowerCase().contains(keyword))) {
                return true;
            }
        }
        if (request.getRoom() != null && request.getRoom().getNumber() != null
                && request.getRoom().getNumber().toLowerCase().contains(keyword)) {
            return true;
        }
        if (request.getHandledBy() != null) {
            String username = request.getHandledBy().getUsername();
            String email = request.getHandledBy().getEmail();
            if ((username != null && username.toLowerCase().contains(keyword))
                    || (email != null && email.toLowerCase().contains(keyword))) {
                return true;
            }
        }
        return request.getDescription() != null
                && request.getDescription().toLowerCase().contains(keyword)
                || (request.getResolutionNotes() != null
                && request.getResolutionNotes().toLowerCase().contains(keyword));
    }
}
