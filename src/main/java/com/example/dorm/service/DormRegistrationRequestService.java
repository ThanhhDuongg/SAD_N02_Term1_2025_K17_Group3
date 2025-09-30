package com.example.dorm.service;

import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.repository.DormRegistrationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DormRegistrationRequestService {

    private final DormRegistrationRequestRepository repository;

    public DormRegistrationRequestService(DormRegistrationRequestRepository repository) {
        this.repository = repository;
    }

    public DormRegistrationRequest submitRequest(DormRegistrationRequest request) {
        if (request.getStatus() == null) {
            request.setStatus(DormRegistrationStatus.PENDING);
        }
        return repository.save(request);
    }

    public List<DormRegistrationRequest> findByStudent(Long studentId) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public List<DormRegistrationRequest> findAll(String statusKeyword, String searchKeyword) {
        List<DormRegistrationRequest> source;
        DormRegistrationStatus statusFilter = parseStatus(statusKeyword);
        if (statusFilter != null) {
            source = repository.findByStatusOrderByCreatedAtDesc(statusFilter);
        } else {
            source = repository.findAllByOrderByCreatedAtDesc();
        }

        if (searchKeyword == null || searchKeyword.isBlank()) {
            return source;
        }

        String normalized = searchKeyword.trim().toLowerCase();
        return source.stream()
                .filter(request -> matchesKeyword(request, normalized))
                .collect(Collectors.toList());
    }

    public Optional<DormRegistrationRequest> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public DormRegistrationRequest updateStatus(Long id, DormRegistrationStatus status, String adminNotes) {
        DormRegistrationRequest request = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));
        if (status != null) {
            request.setStatus(status);
        }
        request.setAdminNotes(adminNotes);
        return repository.save(request);
    }

    @Transactional
    public DormRegistrationRequest updateRequest(Long id, DormRegistrationRequest updated) {
        DormRegistrationRequest request = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));
        request.setDesiredRoomType(updated.getDesiredRoomType());
        request.setPreferredRoomNumber(updated.getPreferredRoomNumber());
        request.setExpectedMoveInDate(updated.getExpectedMoveInDate());
        request.setAdditionalNotes(updated.getAdditionalNotes());
        return repository.save(request);
    }

    public List<DormRegistrationStatus> getAllStatuses() {
        return List.copyOf(EnumSet.allOf(DormRegistrationStatus.class));
    }

    private DormRegistrationStatus parseStatus(String statusKeyword) {
        if (statusKeyword == null || statusKeyword.isBlank()) {
            return null;
        }
        try {
            return DormRegistrationStatus.valueOf(statusKeyword.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private boolean matchesKeyword(DormRegistrationRequest request, String keyword) {
        if (request.getStudent() != null) {
            if (contains(request.getStudent().getName(), keyword) ||
                contains(request.getStudent().getCode(), keyword) ||
                contains(request.getStudent().getEmail(), keyword)) {
                return true;
            }
        }
        return contains(request.getDesiredRoomType(), keyword)
                || contains(request.getPreferredRoomNumber(), keyword)
                || contains(request.getAdditionalNotes(), keyword)
                || contains(request.getAdminNotes(), keyword);
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }
}
