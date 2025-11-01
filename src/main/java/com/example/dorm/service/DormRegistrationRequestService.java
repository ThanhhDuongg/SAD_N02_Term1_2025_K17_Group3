package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.model.PaymentPlan;
import com.example.dorm.model.Room;
import com.example.dorm.model.RoomOccupancyStatus;
import com.example.dorm.model.Student;
import com.example.dorm.repository.DormRegistrationRequestRepository;
import com.example.dorm.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DormRegistrationRequestService {

    private static final Pattern CAPACITY_PATTERN = Pattern.compile("(\\d+)");

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

    @Transactional(readOnly = true)
    public AssignmentPreparation prepareAssignment(Long requestId,
                                                    YearMonth requestedStart,
                                                    YearMonth requestedEnd) {
        DormRegistrationRequest request = repository.findWithStudentAndRoomById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu đăng ký"));

        YearMonth startMonth = determineDefaultStartMonth(request);
        if (requestedStart != null) {
            startMonth = clampStartMonth(request, requestedStart);
        }

        YearMonth endMonth = determineDefaultEndMonth(request, startMonth);
        if (requestedEnd != null) {
            endMonth = clampEndMonth(request, startMonth, requestedEnd);
        }

        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = endMonth.atEndOfMonth();

        List<RoomSuggestion> suggestions = buildRoomSuggestions(request, startDate, endDate);

        return new AssignmentPreparation(request, startMonth, endMonth, suggestions);
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

        LocalDate normalizedStart = requireDate(startDate, "Vui lòng chọn ngày bắt đầu ở").withDayOfMonth(1);
        LocalDate normalizedEnd = requireDate(endDate, "Vui lòng chọn ngày kết thúc ở");
        normalizedEnd = normalizedEnd.withDayOfMonth(normalizedEnd.lengthOfMonth());
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
        long months = ChronoUnit.MONTHS.between(YearMonth.from(start), YearMonth.from(end)) + 1;
        if (months > 12) {
            throw new IllegalStateException("Mỗi hợp đồng chỉ được kéo dài tối đa 12 tháng. Vui lòng tách thành hợp đồng mới.");
        }
    }

    private List<RoomSuggestion> buildRoomSuggestions(DormRegistrationRequest request,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {
        List<Room> candidates = new ArrayList<>(loadCandidateRooms(request));
        if (candidates.isEmpty()) {
            return List.of();
        }

        List<RoomSuggestion> suggestions = new ArrayList<>();
        String preferredNumber = request.getPreferredRoomNumber();
        for (Room room : candidates) {
            if (room.getCapacity() <= 0 || room.getOccupancyStatus() != RoomOccupancyStatus.AVAILABLE) {
                continue;
            }

            long occupied = contractService.countBlockingContractsForRoom(room.getId(), startDate, endDate, null);
            int availableSlots = room.getCapacity() - (int) occupied;
            if (availableSlots <= 0) {
                continue;
            }

            List<String> occupants = contractService.findBlockingContractsForRoom(room.getId(), startDate, endDate, null)
                    .stream()
                    .map(contract -> contract.getStudent() != null ? contract.getStudent().getName() : "(Chưa xác định)")
                    .sorted()
                    .collect(Collectors.toList());

            boolean matchesPreference = preferredNumber != null
                    && !preferredNumber.isBlank()
                    && preferredNumber.equalsIgnoreCase(room.getNumber());

            suggestions.add(new RoomSuggestion(
                    room.getId(),
                    room.getNumber(),
                    room.getBuilding() != null ? room.getBuilding().getName() : null,
                    room.getRoomType() != null ? room.getRoomType().getName() : room.getType(),
                    room.getCapacity(),
                    availableSlots,
                    room.getCapacity() - availableSlots,
                    resolveRoomPrice(room),
                    matchesPreference,
                    occupants
            ));
        }

        suggestions.sort(Comparator
                .comparing(RoomSuggestion::matchesPreference).reversed()
                .thenComparing(RoomSuggestion::availableSlots, Comparator.reverseOrder())
                .thenComparing(RoomSuggestion::price)
                .thenComparing(RoomSuggestion::number, String.CASE_INSENSITIVE_ORDER));

        return suggestions;
    }

    private List<Room> loadCandidateRooms(DormRegistrationRequest request) {
        String desiredType = request.getDesiredRoomType();
        List<Room> rooms = new ArrayList<>();
        if (desiredType != null && !desiredType.isBlank()) {
            rooms.addAll(roomRepository.findAvailableRoomsByType(desiredType.trim(), RoomOccupancyStatus.AVAILABLE));
        } else {
            rooms.addAll(roomRepository.findAvailableRoomsByType(null, RoomOccupancyStatus.AVAILABLE));
        }

        if (rooms.isEmpty() && desiredType != null && !desiredType.isBlank()) {
            rooms.addAll(roomRepository.findAvailableRoomsByType(null, RoomOccupancyStatus.AVAILABLE));
        }

        String preferredNumber = request.getPreferredRoomNumber();
        if (preferredNumber != null && !preferredNumber.isBlank()) {
            roomRepository.findByNumberWithDetails(preferredNumber.trim())
                    .filter(room -> room.getOccupancyStatus() == RoomOccupancyStatus.AVAILABLE)
                    .ifPresent(room -> {
                        boolean exists = rooms.stream().anyMatch(candidate -> Objects.equals(candidate.getId(), room.getId()));
                        if (!exists) {
                            rooms.add(0, room);
                        }
                    });
        }

        return rooms;
    }

    private int resolveRoomPrice(Room room) {
        if (room.getRoomType() != null) {
            return room.getRoomType().getCurrentPrice();
        }
        return room.getPrice();
    }

    private YearMonth determineDefaultStartMonth(DormRegistrationRequest request) {
        if (request.getApprovedStartDate() != null) {
            return clampStartMonth(request, YearMonth.from(request.getApprovedStartDate()));
        }
        if (request.getExpectedMoveInDate() != null) {
            return clampStartMonth(request, YearMonth.from(request.getExpectedMoveInDate()));
        }
        DormRegistrationPeriod period = request.getPeriod();
        if (period != null && period.getStartTime() != null) {
            return clampStartMonth(request, YearMonth.from(period.getStartTime()));
        }
        return YearMonth.from(LocalDate.now());
    }

    private YearMonth determineDefaultEndMonth(DormRegistrationRequest request, YearMonth startMonth) {
        YearMonth candidate;
        if (request.getApprovedEndDate() != null) {
            candidate = YearMonth.from(request.getApprovedEndDate());
        } else {
            DormRegistrationPeriod period = request.getPeriod();
            if (period != null && period.getEndTime() != null) {
                candidate = YearMonth.from(period.getEndTime());
            } else {
                candidate = startMonth.plusMonths(5);
            }
        }
        return clampEndMonth(request, startMonth, candidate);
    }

    private YearMonth clampStartMonth(DormRegistrationRequest request, YearMonth candidate) {
        DormRegistrationPeriod period = request.getPeriod();
        if (period != null && period.getStartTime() != null) {
            YearMonth min = YearMonth.from(period.getStartTime());
            if (candidate.isBefore(min)) {
                return min;
            }
        }
        return candidate;
    }

    private YearMonth clampEndMonth(DormRegistrationRequest request,
                                    YearMonth startMonth,
                                    YearMonth candidate) {
        YearMonth adjusted = candidate.isBefore(startMonth) ? startMonth : candidate;
        YearMonth max = startMonth.plusMonths(11);
        if (adjusted.isAfter(max)) {
            adjusted = max;
        }
        return adjusted;
    }

    private void ensureRoomMatchesPreference(DormRegistrationRequest request, Room room) {
        String desiredType = request.getDesiredRoomType();
        if (desiredType == null || desiredType.isBlank()) {
            return;
        }
        String trimmedDesired = desiredType.trim();
        String actualType = room.getRoomType() != null ? room.getRoomType().getName() : room.getType();
        if (actualType != null && actualType.equalsIgnoreCase(trimmedDesired)) {
            return;
        }

        Integer desiredCapacity = extractCapacity(trimmedDesired);
        if (desiredCapacity != null) {
            Integer roomCapacity = room.getRoomType() != null ? room.getRoomType().getCapacity() : room.getCapacity();
            if (roomCapacity != null && roomCapacity.equals(desiredCapacity)) {
                return;
            }
        }

        throw new IllegalStateException("Phòng được chọn không đúng với loại phòng sinh viên đăng ký");
    }

    private Integer extractCapacity(String desiredType) {
        Matcher matcher = CAPACITY_PATTERN.matcher(desiredType);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
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

    public record AssignmentPreparation(DormRegistrationRequest request,
                                        YearMonth startMonth,
                                        YearMonth endMonth,
                                        List<RoomSuggestion> roomSuggestions) {
    }

    public record RoomSuggestion(Long id,
                                 String number,
                                 String buildingName,
                                 String roomType,
                                 int capacity,
                                 int availableSlots,
                                 int occupiedSlots,
                                 int price,
                                 boolean matchesPreference,
                                 List<String> occupants) {
    }
}
