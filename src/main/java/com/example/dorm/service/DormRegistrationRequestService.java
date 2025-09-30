package com.example.dorm.service;

import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.model.Student;
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
    private final DormRegistrationPeriodService periodService;

    public DormRegistrationRequestService(DormRegistrationRequestRepository repository,
                                          DormRegistrationPeriodService periodService) {
        this.repository = repository;
        this.periodService = periodService;
    }

    @Transactional
    public DormRegistrationRequest submitRequest(Student student, DormRegistrationRequest request) {
        DormRegistrationPeriod period = periodService.getOpenPeriod()
                .orElseThrow(() -> new IllegalStateException("Hiện chưa có đợt đăng ký nào đang mở."));

        if (repository.existsByStudentIdAndPeriodId(student.getId(), period.getId())) {
            throw new IllegalStateException("Bạn đã gửi đăng ký trong đợt này. Vui lòng chờ kết quả.");
        }

        if (request.getExpectedMoveInDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày dự kiến vào ở.");
        }

        if (period.getCapacity() != null && periodService.countSubmittedRequests(period.getId()) >= period.getCapacity()) {
            throw new IllegalStateException("Đợt đăng ký đã đủ số lượng hồ sơ.");
        }

        request.setStudent(student);
        request.setPeriod(period);
        request.setStatus(DormRegistrationStatus.PENDING);
        return repository.save(request);
    }

    public List<DormRegistrationRequest> findByStudent(Long studentId) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public List<DormRegistrationRequest> findAll(Long periodId, String statusKeyword, String searchKeyword) {
        List<DormRegistrationRequest> source;
        DormRegistrationStatus statusFilter = parseStatus(statusKeyword);
        if (periodId != null) {
            if (statusFilter != null) {
                source = repository.findByPeriodIdAndStatusOrderByCreatedAtDesc(periodId, statusFilter);
            } else {
                source = repository.findByPeriodIdOrderByCreatedAtDesc(periodId);
            }
        } else if (statusFilter != null) {
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

    public boolean hasSubmissionInPeriod(Long studentId, Long periodId) {
        if (periodId == null) {
            return false;
        }
        return repository.existsByStudentIdAndPeriodId(studentId, periodId);
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
        if (request.getPeriod() != null && contains(request.getPeriod().getName(), keyword)) {
            return true;
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
