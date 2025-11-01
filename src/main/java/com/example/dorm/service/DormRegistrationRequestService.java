package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.model.PaymentPlan;
import com.example.dorm.model.Room;
import com.example.dorm.model.Student;
import com.example.dorm.repository.DormRegistrationRequestRepository;
import com.example.dorm.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class DormRegistrationRequestService {

    private final DormRegistrationRequestRepository repository;
    private final DormRegistrationPeriodService periodService;
    private final ContractService contractService;
    private final RoomRepository roomRepository;

    public DormRegistrationRequestService(DormRegistrationRequestRepository repository,
                                          DormRegistrationPeriodService periodService,
                                          ContractService contractService,
                                          RoomRepository roomRepository) {
        this.repository = repository;
        this.periodService = periodService;
        this.contractService = contractService;
        this.roomRepository = roomRepository;
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
        return repository.findWithStudentAndRoomById(id);
    }

    @Transactional
    public DormRegistrationAssignmentResult approveAndAssign(Long requestId,
                                                             Long roomId,
                                                             LocalDate startDate,
                                                             LocalDate endDate,
                                                             PaymentPlan paymentPlan,
                                                             Integer billingDay,
                                                             boolean forcePriority,
                                                             String adminNotes) {
        DormRegistrationRequest request = repository.findWithStudentAndRoomById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu đăng ký"));
        if (request.getStudent() == null) {
            throw new IllegalStateException("Hồ sơ chưa gắn với sinh viên hợp lệ");
        }
        if (request.getStatus() == DormRegistrationStatus.REJECTED) {
            throw new IllegalStateException("Yêu cầu đã bị từ chối, không thể phê duyệt lại");
        }
        if (request.getStatus() == DormRegistrationStatus.APPROVED) {
            throw new IllegalStateException("Yêu cầu đã được xử lý");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng được chọn"));

        ensureRoomMatchesPreference(request, room);
        if (!forcePriority) {
            enforceQueueDiscipline(request);
        }

        LocalDate normalizedStart = requireDate(startDate, "Vui lòng chọn ngày bắt đầu ở");
        LocalDate normalizedEnd = requireDate(endDate, "Vui lòng chọn ngày kết thúc ở");
        if (normalizedEnd.isBefore(normalizedStart)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (request.getExpectedMoveInDate() != null && normalizedStart.isBefore(request.getExpectedMoveInDate())) {
            throw new IllegalStateException("Ngày vào ở không thể sớm hơn thời điểm sinh viên đăng ký");
        }

        ensureWithinPeriod(request, normalizedStart, normalizedEnd);

        Contract contract = new Contract();
        contract.setStudent(request.getStudent());
        contract.setRoom(room);
        contract.setStartDate(normalizedStart);
        contract.setEndDate(normalizedEnd);
        contract.setPaymentPlan(paymentPlan != null ? paymentPlan : PaymentPlan.MONTHLY);
        contract.setBillingDayOfMonth(billingDay);

        Contract savedContract = contractService.createContract(contract);

        request.setStatus(DormRegistrationStatus.APPROVED);
        request.setApprovedStartDate(normalizedStart);
        request.setApprovedEndDate(normalizedEnd);
        if (adminNotes != null && !adminNotes.isBlank()) {
            request.setAdminNotes(adminNotes.trim());
        }
        repository.save(request);

        return new DormRegistrationAssignmentResult(request, savedContract);
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

    private void ensureWithinPeriod(DormRegistrationRequest request, LocalDate start, LocalDate end) {
        DormRegistrationPeriod period = request.getPeriod();
        if (period == null) {
            return;
        }
        if (period.getStartTime() != null && start.isBefore(period.getStartTime().toLocalDate())) {
            throw new IllegalStateException("Thời gian ở phải nằm trong đợt đăng ký");
        }
        if (period.getEndTime() != null && end.isAfter(period.getEndTime().toLocalDate())) {
            throw new IllegalStateException("Thời gian ở vượt quá giới hạn đợt đăng ký");
        }
    }

    private void ensureRoomMatchesPreference(DormRegistrationRequest request, Room room) {
        String desiredType = request.getDesiredRoomType();
        if (desiredType == null || desiredType.isBlank()) {
            return;
        }
        String actualType = room.getRoomType() != null ? room.getRoomType().getName() : room.getType();
        if (actualType == null || !actualType.equalsIgnoreCase(desiredType.trim())) {
            throw new IllegalStateException("Phòng được chọn không đúng với loại phòng sinh viên đăng ký");
        }
    }

    private void enforceQueueDiscipline(DormRegistrationRequest request) {
        DormRegistrationPeriod period = request.getPeriod();
        String desiredType = request.getDesiredRoomType();
        if (period == null || desiredType == null || desiredType.isBlank()) {
            return;
        }
        repository.findFirstByPeriod_IdAndDesiredRoomTypeIgnoreCaseAndStatusOrderByCreatedAtAsc(
                        period.getId(), desiredType.trim(), DormRegistrationStatus.PENDING)
                .filter(first -> !Objects.equals(first.getId(), request.getId()))
                .ifPresent(first -> {
                    if (first.getCreatedAt() != null && request.getCreatedAt() != null
                            && first.getCreatedAt().isBefore(request.getCreatedAt())) {
                        throw new IllegalStateException("Vẫn còn hồ sơ gửi sớm hơn cho loại phòng này");
                    }
                });
    }

    private LocalDate requireDate(LocalDate value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
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

    public record DormRegistrationAssignmentResult(DormRegistrationRequest request, Contract contract) {
    }
}
