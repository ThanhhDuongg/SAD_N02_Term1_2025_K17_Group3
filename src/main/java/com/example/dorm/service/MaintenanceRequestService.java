package com.example.dorm.service;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.User;
import com.example.dorm.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestService(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        normalizeRequest(request);
        return maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequest> getRequestsByStudent(Long studentId) {
        return maintenanceRequestRepository.findByStudent_IdOrderByUpdatedAtDesc(studentId);
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceRequestRepository.findAll().stream()
                .sorted(requestRecencyComparator())
                .toList();
    }

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return getAllRequests();
        }
        return maintenanceRequestRepository.findByStatusIgnoreCaseOrderByUpdatedAtDesc(status.trim().toUpperCase())
                .stream()
                .sorted(requestRecencyComparator())
                .toList();
    }

    public List<MaintenanceRequest> getRequests(String status, String type, String keyword, String assignedUsername) {
        String normalizedStatus = status != null && !status.isBlank() ? status.trim().toUpperCase() : null;
        String normalizedType = type != null && !type.isBlank() ? type.trim().toUpperCase() : null;
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword.trim().toLowerCase() : null;
        String normalizedAssignee = assignedUsername != null && !assignedUsername.isBlank()
                ? assignedUsername.trim()
                : null;

        List<MaintenanceRequest> baseRequests;
        boolean filterByStatus = normalizedStatus != null;
        boolean filterByAssignee = normalizedAssignee != null;

        if (filterByStatus && filterByAssignee) {
            baseRequests = maintenanceRequestRepository
                    .findByStatusIgnoreCaseAndHandledBy_UsernameIgnoreCaseOrderByUpdatedAtDesc(
                            normalizedStatus, normalizedAssignee);
        } else if (filterByStatus) {
            baseRequests = maintenanceRequestRepository
                    .findByStatusIgnoreCaseOrderByUpdatedAtDesc(normalizedStatus);
        } else if (filterByAssignee) {
            baseRequests = maintenanceRequestRepository
                    .findByHandledBy_UsernameIgnoreCaseOrderByUpdatedAtDesc(normalizedAssignee);
        } else {
            baseRequests = getAllRequests();
        }

        Stream<MaintenanceRequest> stream = baseRequests.stream();

        if (normalizedType != null) {
            stream = stream.filter(request -> normalizedType.equalsIgnoreCase(request.getRequestType()));
        }

        if (normalizedKeyword != null) {
            stream = stream.filter(request -> matchesKeyword(request, normalizedKeyword));
        }

        return stream.sorted(requestRecencyComparator())
                .toList();
    }

    public Optional<MaintenanceRequest> getRequest(Long id) {
        return maintenanceRequestRepository.findById(id);
    }

    public MaintenanceRequest getRequiredRequest(Long id) {
        return getRequest(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance request not found"));
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

    public MaintenanceRequest assignHandler(Long id, User handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Không tìm thấy nhân viên xử lý");
        }
        MaintenanceRequest request = getRequiredRequest(id);
        request.setHandledBy(handler);
        String status = request.getStatus();
        if (status == null || status.isBlank() || "PENDING".equalsIgnoreCase(status)) {
            request.setStatus("IN_PROGRESS");
        }
        request.setUpdatedAt(LocalDateTime.now());
        return maintenanceRequestRepository.save(request);
    }

    public MaintenanceRequest unassignHandler(Long id) {
        MaintenanceRequest request = getRequiredRequest(id);
        request.setHandledBy(null);
        String status = request.getStatus();
        if (!"COMPLETED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status)) {
            request.setStatus("PENDING");
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

    private Comparator<MaintenanceRequest> requestRecencyComparator() {
        Comparator<MaintenanceRequest> byUpdatedAt = Comparator.comparing(
                MaintenanceRequest::getUpdatedAt,
                Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<MaintenanceRequest> byCreatedAt = Comparator.comparing(
                MaintenanceRequest::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder()));
        return byUpdatedAt.thenComparing(byCreatedAt).reversed();
    }
}
