package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.PaymentPlan;
import com.example.dorm.model.Room;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final FeeService feeService;

    public static final Set<String> NON_BLOCKING_STATUSES = Set.of("CANCELLED", "TERMINATED", "ENDED", "EXPIRED");

    public ContractService(ContractRepository contractRepository,
                           StudentRepository studentRepository,
                           RoomRepository roomRepository,
                           FeeService feeService) {
        this.contractRepository = contractRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.feeService = feeService;
    }

    public Page<Contract> getAllContracts(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Optional<Contract> getContract(Long id) {
        return contractRepository.findById(id);
    }

    public Contract getRequiredContract(Long id) {
        return getContract(id).orElseThrow(() -> new IllegalArgumentException("Contract not found"));
    }

    public Contract createContract(Contract contract) {
        Long roomId = extractRoomId(contract);
        Long studentId = extractStudentId(contract);
        LocalDate startDate = requireStartDate(contract);
        LocalDate endDate = requireEndDate(contract);

        Room room = getRequiredRoom(roomId);
        validateContractWindow(room, studentId, startDate, endDate, null);

        contract.setRoom(room);
        contract.setPaymentPlan(contract.getPaymentPlan() != null ? contract.getPaymentPlan() : PaymentPlan.MONTHLY);
        contract.setBillingDayOfMonth(normalizeBillingDay(contract.getBillingDayOfMonth(), startDate));
        contract.setStatus(normalizeStatus(contract.getStatus(), startDate, endDate));

        if (studentId != null) {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(room);
            studentRepository.save(student);
            contract.setStudent(student);
        }

        Contract saved = contractRepository.save(contract);
        feeService.synchronizeRentFees(saved);
        return saved;
    }

    public Contract updateContract(Long id, Contract contract) {
        Contract existing = getRequiredContract(id);
        Long newStudentId = extractStudentId(contract);
        Long existingStudentId = existing.getStudent() != null ? existing.getStudent().getId() : null;
        Long newRoomId = extractRoomId(contract);
        LocalDate newStartDate = requireStartDate(contract);
        LocalDate newEndDate = requireEndDate(contract);

        Room room = getRequiredRoom(newRoomId);
        boolean removeStudent = contract.getStudent() == null || contract.getStudent().getId() == null;
        Long targetStudentId = removeStudent ? null : newStudentId;

        validateContractWindow(room, targetStudentId, newStartDate, newEndDate, existing.getId());

        if (!removeStudent && newStudentId != null) {
            var student = studentRepository.findById(newStudentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(room);
            studentRepository.save(student);
            existing.setStudent(student);
        } else {
            if (existingStudentId != null) {
                studentRepository.findById(existingStudentId).ifPresent(s -> {
                    s.setRoom(null);
                    studentRepository.save(s);
                });
            }
            existing.setStudent(null);
        }

        existing.setRoom(room);
        existing.setStartDate(newStartDate);
        existing.setEndDate(newEndDate);
        existing.setStatus(normalizeStatus(contract.getStatus(), newStartDate, newEndDate));
        existing.setPaymentPlan(contract.getPaymentPlan() != null ? contract.getPaymentPlan() : PaymentPlan.MONTHLY);
        existing.setBillingDayOfMonth(normalizeBillingDay(contract.getBillingDayOfMonth(), newStartDate));

        Contract saved = contractRepository.save(existing);
        feeService.synchronizeRentFees(saved);
        return saved;
    }

    @Transactional
    public void deleteContract(Long id) {
        contractRepository.findById(id).ifPresent(contract -> {
            if (contract.getStudent() != null) {
                studentRepository.findById(contract.getStudent().getId()).ifPresent(student -> {
                    student.setRoom(null);
                    studentRepository.save(student);
                });
            }
            feeService.removeFeesForContract(contract.getId());
            contractRepository.delete(contract);
        });
    }

    public List<Contract> getContractsByStudent(Long studentId) {
        return contractRepository.findByStudent_Id(studentId);
    }

    public List<Contract> getContractsByRoom(Long roomId) {
        if (roomId == null) {
            return List.of();
        }
        return contractRepository.findByRoom_Id(roomId);
    }

    public Page<Contract> searchContracts(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return contractRepository.findAll(pageable);
        }
        return contractRepository
                .searchByStudentWordOrCodeOrRoomOrStatus(search, pageable);
    }

    public Page<Contract> searchContractsAutocomplete(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return contractRepository.findAll(pageable);
        }
        return contractRepository.searchByIdOrStudentWord(search, pageable);
    }

    public Contract findLatestContractByStudentId(Long studentId) {
        return contractRepository.findTopByStudent_IdOrderByEndDateDesc(studentId);
    }

    public long countContracts() {
        return contractRepository.count();
    }

    private Room getRequiredRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    private LocalDate requireStartDate(Contract contract) {
        if (contract.getStartDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu hợp đồng");
        }
        return contract.getStartDate();
    }

    private LocalDate requireEndDate(Contract contract) {
        if (contract.getEndDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày kết thúc hợp đồng");
        }
        return contract.getEndDate();
    }

    private void validateContractWindow(Room room,
                                        Long studentId,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        Long excludeContractId) {
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Hợp đồng phải có thời gian bắt đầu và kết thúc");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc hợp đồng phải sau ngày bắt đầu");
        }

        if (studentId != null) {
            List<Contract> overlaps = contractRepository
                    .findOverlappingByStudent(studentId, startDate, endDate, excludeContractId);
            overlaps.stream()
                    .filter(contract -> isBlockingStatus(contract.getStatus()))
                    .findAny()
                    .ifPresent(conflict -> {
                        throw new IllegalStateException("Sinh viên đã có hợp đồng trong khoảng thời gian này");
                    });
        }

        long activeCount = countBlockingContractsForRoom(room.getId(), startDate, endDate, excludeContractId);
        if (activeCount >= room.getCapacity()) {
            throw new IllegalStateException("Phòng đã đủ người trong khoảng thời gian này");
        }
    }

    public boolean isBlockingStatus(String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        return !NON_BLOCKING_STATUSES.contains(status.trim().toUpperCase());
    }

    public long countBlockingContractsForRoom(Long roomId,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              Long excludeContractId) {
        if (roomId == null) {
            return 0;
        }
        return contractRepository.countBlockingContractsByRoom(
                roomId,
                startDate,
                endDate,
                excludeContractId,
                NON_BLOCKING_STATUSES
        );
    }

    @Transactional(readOnly = true)
    public List<Contract> findBlockingContractsForRoom(Long roomId,
                                                       LocalDate startDate,
                                                       LocalDate endDate,
                                                       Long excludeContractId) {
        if (roomId == null) {
            return List.of();
        }
        return contractRepository.findOverlappingByRoom(roomId, startDate, endDate, excludeContractId)
                .stream()
                .filter(contract -> isBlockingStatus(contract.getStatus()))
                .collect(Collectors.toList());
    }

    private String normalizeStatus(String requestedStatus, LocalDate startDate, LocalDate endDate) {
        if (requestedStatus != null && !requestedStatus.isBlank()) {
            return requestedStatus;
        }
        LocalDate today = LocalDate.now();
        if (endDate.isBefore(today)) {
            return "EXPIRED";
        }
        if (startDate.isAfter(today)) {
            return "UPCOMING";
        }
        return "ACTIVE";
    }

    private Integer normalizeBillingDay(Integer billingDayOfMonth, LocalDate startDate) {
        if (billingDayOfMonth == null || billingDayOfMonth < 1) {
            return startDate != null ? startDate.getDayOfMonth() : 1;
        }
        return Math.min(Math.max(1, billingDayOfMonth), 31);
    }

    private Long extractStudentId(Contract contract) {
        return contract.getStudent() != null ? contract.getStudent().getId() : null;
    }

    private Long extractRoomId(Contract contract) {
        if (contract.getRoom() == null || contract.getRoom().getId() == null) {
            throw new IllegalArgumentException("Room must be provided");
        }
        return contract.getRoom().getId();
    }
}
