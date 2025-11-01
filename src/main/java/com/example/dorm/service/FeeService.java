package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeScope;
import com.example.dorm.model.FeeType;
import com.example.dorm.model.PaymentPlan;
import com.example.dorm.model.PaymentStatus;
import com.example.dorm.model.Room;
import com.example.dorm.model.RoomType;
import com.example.dorm.model.RoomTypePriceHistory;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.FeeRepository;
import com.example.dorm.repository.RoomTypePriceHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private static final Set<String> INACTIVE_CONTRACT_STATUSES = Set.of("CANCELLED", "TERMINATED", "ENDED", "EXPIRED");

    private final FeeRepository feeRepository;
    private final ContractRepository contractRepository;
    private final RoomTypePriceHistoryRepository roomTypePriceHistoryRepository;

    public FeeService(FeeRepository feeRepository,
                      ContractRepository contractRepository,
                      RoomTypePriceHistoryRepository roomTypePriceHistoryRepository) {
        this.feeRepository = feeRepository;
        this.contractRepository = contractRepository;
        this.roomTypePriceHistoryRepository = roomTypePriceHistoryRepository;
    }

    public Page<Fee> getAllFees(Pageable pageable) {
        return feeRepository.findAll(pageable);
    }

    public int refreshOverdueStatuses() {
        LocalDate today = LocalDate.now();
        List<Fee> fees = feeRepository.findAll();
        int updates = 0;
        for (Fee fee : fees) {
            if (fee.getDueDate() == null || fee.getPaymentStatus() == PaymentStatus.PAID) {
                continue;
            }
            if (fee.getDueDate().isBefore(today) && fee.getPaymentStatus() == PaymentStatus.UNPAID) {
                fee.setPaymentStatus(PaymentStatus.OVERDUE);
                updates++;
            } else if (!fee.getDueDate().isBefore(today) && fee.getPaymentStatus() == PaymentStatus.OVERDUE) {
                fee.setPaymentStatus(PaymentStatus.UNPAID);
                updates++;
            }
        }
        if (updates > 0) {
            feeRepository.saveAll(fees);
        }
        return updates;
    }

    public Optional<Fee> getFee(Long id) {
        return feeRepository.findById(id);
    }

    public Fee getRequiredFee(Long id) {
        return getFee(id).orElseThrow(() -> new IllegalArgumentException("Fee not found"));
    }

    public void createFee(Fee fee, BigDecimal totalAmount) {
        FeeScope scope = fee.getScope() != null ? fee.getScope() : FeeScope.INDIVIDUAL;
        if (scope == FeeScope.ROOM) {
            distributeRoomFee(fee, normalizeAmount(totalAmount), fee.getRoomId());
        } else {
            persistIndividualFee(fee, normalizeAmount(totalAmount));
        }
    }

    public void updateFee(Long id, Fee fee, BigDecimal totalAmount) {
        Fee existing = getRequiredFee(id);
        FeeScope newScope = fee.getScope() != null ? fee.getScope() : FeeScope.INDIVIDUAL;
        FeeScope currentScope = existing.getScope() != null ? existing.getScope() : FeeScope.INDIVIDUAL;
        BigDecimal normalizedTotal = normalizeAmount(totalAmount);

        if (currentScope == FeeScope.ROOM && newScope == FeeScope.ROOM) {
            updateRoomFeeGroup(existing, fee, normalizedTotal, fee.getRoomId());
        } else if (currentScope == FeeScope.ROOM && newScope == FeeScope.INDIVIDUAL) {
            convertRoomFeeToIndividual(existing, fee, normalizedTotal);
        } else if (currentScope == FeeScope.INDIVIDUAL && newScope == FeeScope.ROOM) {
            convertIndividualFeeToRoom(existing, fee, normalizedTotal);
        } else {
            updateIndividual(existing, fee, normalizedTotal);
        }
    }

    public void deleteFee(Long id) {
        feeRepository.deleteById(id);
    }

    public List<Fee> getFeesByStudent(Long studentId) {
        return feeRepository.findByContract_Student_Id(studentId);
    }

    public List<Fee> getUnpaidFeesByStudent(Long studentId) {
        return feeRepository.findByContract_Student_Id(studentId).stream()
                .filter(fee -> fee.getPaymentStatus() != PaymentStatus.PAID)
                .toList();
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public Page<Fee> searchFees(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return feeRepository.findAll(pageable);
        }
        // try to match fee type
        FeeType type = null;
        for (FeeType t : FeeType.values()) {
            if (t.name().equalsIgnoreCase(search)) {
                type = t;
                break;
            }
        }
        if (type != null) {
            return feeRepository
                    .searchByTypeOrContractStudentWord(type, search, pageable);
        }
        return feeRepository
                .searchByContractStudentWord(search, pageable);
    }

    public long countFees() {
        return feeRepository.count();
    }

    public List<Fee> synchronizeRentFees(Contract contract) {
        if (contract == null || contract.getId() == null) {
            return List.of();
        }

        PaymentPlan plan = contract.getPaymentPlan() != null ? contract.getPaymentPlan() : PaymentPlan.MONTHLY;
        List<Fee> existing = feeRepository.findByContract_IdAndType(contract.getId(), FeeType.RENT);
        List<Fee> orphaned = existing.stream()
                .filter(fee -> fee.getDueDate() == null)
                .toList();
        Map<LocalDate, Fee> existingByDueDate = existing.stream()
                .filter(fee -> fee.getDueDate() != null)
                .collect(Collectors.toMap(Fee::getDueDate, Function.identity(), (left, right) -> left, HashMap::new));

        List<Fee> result = new ArrayList<>();
        List<RoomTypePriceHistory> priceHistory = resolvePriceHistory(contract.getRoom());

        if (!orphaned.isEmpty()) {
            feeRepository.deleteAll(orphaned);
        }

        if (plan == PaymentPlan.FULL_TERM) {
            result.add(buildFullTermFee(contract, existingByDueDate, priceHistory));
        } else {
            result.addAll(buildMonthlyFees(contract, existingByDueDate, priceHistory));
        }

        if (!existingByDueDate.isEmpty()) {
            feeRepository.deleteAll(existingByDueDate.values());
        }
        if (!result.isEmpty()) {
            feeRepository.saveAll(result);
        }
        return result;
    }

    public void removeFeesForContract(Long contractId) {
        if (contractId == null) {
            return;
        }
        List<Fee> fees = feeRepository.findByContract_Id(contractId);
        if (!fees.isEmpty()) {
            feeRepository.deleteAll(fees);
        }
    }

    private void persistIndividualFee(Fee fee, BigDecimal totalAmount) {
        Contract contract = getRequiredContract(fee);
        fee.setContract(contract);
        fee.setScope(FeeScope.INDIVIDUAL);
        fee.setAmount(totalAmount);
        fee.setTotalAmount(totalAmount);
        fee.setGroupCode(null);
        fee.setPaymentStatus(normalizePaymentStatus(fee.getPaymentStatus()));
        feeRepository.save(fee);
    }

    private void distributeRoomFee(Fee template, BigDecimal totalAmount, Long requestedRoomId) {
        Contract baseContract = findContractIfPresent(template);
        Long roomId = resolveRoomId(requestedRoomId, baseContract);
        List<Contract> roomContracts = findActiveRoomContracts(roomId, template.getDueDate());
        if (roomContracts.isEmpty()) {
            throw new IllegalStateException("Phòng chưa có sinh viên phù hợp để phân bổ phí.");
        }

        String groupCode = template.getGroupCode();
        if (groupCode == null || groupCode.isBlank()) {
            groupCode = UUID.randomUUID().toString();
        }

        Map<Long, Fee> existing = Collections.emptyMap();
        distributeToContracts(template, totalAmount, groupCode, roomContracts, existing);
    }

    private void updateRoomFeeGroup(Fee existing, Fee template, BigDecimal totalAmount, Long requestedRoomId) {
        String groupCode = existing.getGroupCode();
        if (groupCode == null || groupCode.isBlank()) {
            groupCode = UUID.randomUUID().toString();
        }

        List<Fee> groupMembers = feeRepository.findByGroupCode(groupCode);
        if (groupMembers.isEmpty()) {
            groupMembers = List.of(existing);
        }

        Contract baseContract = existing.getContract();
        Contract templateContract = findContractIfPresent(template);
        if (templateContract != null) {
            baseContract = templateContract;
        }
        Long roomId = resolveRoomId(requestedRoomId, baseContract);
        List<Contract> roomContracts = findActiveRoomContracts(roomId, template.getDueDate());
        if (roomContracts.isEmpty()) {
            throw new IllegalStateException("Phòng chưa có sinh viên phù hợp để phân bổ phí.");
        }

        Set<Long> contractIds = roomContracts.stream()
                .map(Contract::getId)
                .collect(Collectors.toSet());

        List<Fee> removable = groupMembers.stream()
                .filter(f -> f.getContract() == null || !contractIds.contains(f.getContract().getId()))
                .collect(Collectors.toList());
        if (!removable.isEmpty()) {
            feeRepository.deleteAll(removable);
        }

        Map<Long, Fee> existingByContract = groupMembers.stream()
                .filter(f -> f.getContract() != null && contractIds.contains(f.getContract().getId()))
                .collect(Collectors.toMap(f -> f.getContract().getId(), f -> f));

        distributeToContracts(template, totalAmount, groupCode, roomContracts, existingByContract);
    }

    private void convertRoomFeeToIndividual(Fee existing, Fee template, BigDecimal totalAmount) {
        String groupCode = existing.getGroupCode();
        if (groupCode != null && !groupCode.isBlank()) {
            List<Fee> groupMembers = feeRepository.findByGroupCode(groupCode);
            groupMembers.stream()
                    .filter(member -> !Objects.equals(member.getId(), existing.getId()))
                    .forEach(feeRepository::delete);
        }

        Contract contract = getRequiredContract(template);
        existing.setContract(contract);
        existing.setScope(FeeScope.INDIVIDUAL);
        existing.setGroupCode(null);
        existing.setType(template.getType());
        existing.setDueDate(template.getDueDate());
        existing.setPaymentStatus(determineTargetStatus(template.getPaymentStatus(), existing.getPaymentStatus()));
        existing.setAmount(totalAmount);
        existing.setTotalAmount(totalAmount);
        feeRepository.save(existing);
    }

    private void convertIndividualFeeToRoom(Fee existing, Fee template, BigDecimal totalAmount) {
        feeRepository.delete(existing);
        distributeRoomFee(template, totalAmount, template.getRoomId());
    }

    private void updateIndividual(Fee existing, Fee template, BigDecimal totalAmount) {
        Contract contract = getRequiredContract(template);
        existing.setContract(contract);
        existing.setScope(FeeScope.INDIVIDUAL);
        existing.setGroupCode(null);
        existing.setType(template.getType());
        existing.setDueDate(template.getDueDate());
        existing.setPaymentStatus(determineTargetStatus(template.getPaymentStatus(), existing.getPaymentStatus()));
        existing.setAmount(totalAmount);
        existing.setTotalAmount(totalAmount);
        feeRepository.save(existing);
    }

    private void distributeToContracts(Fee template,
                                       BigDecimal totalAmount,
                                       String groupCode,
                                       List<Contract> contracts,
                                       Map<Long, Fee> existingByContract) {
        int participantCount = contracts.size();
        if (participantCount == 0) {
            throw new IllegalStateException("Phòng chưa có thành viên để phân bổ phí.");
        }

        List<Contract> sortedContracts = contracts.stream()
                .sorted(Comparator.comparing(Contract::getId))
                .toList();

        int scale = Math.max(totalAmount.scale(), 0);
        BigDecimal divisor = BigDecimal.valueOf(participantCount);
        BigDecimal baseShare = totalAmount.divide(divisor, scale, RoundingMode.DOWN);
        BigDecimal allocated = baseShare.multiply(divisor);
        BigDecimal remainder = totalAmount.subtract(allocated);
        BigDecimal unit = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        List<Fee> toPersist = new ArrayList<>();
        for (Contract contract : sortedContracts) {
            Fee fee = existingByContract.getOrDefault(contract.getId(), new Fee());
            fee.setContract(contract);
            fee.setScope(FeeScope.ROOM);
            fee.setGroupCode(groupCode);
            fee.setType(template.getType());
            fee.setDueDate(template.getDueDate());
            fee.setPaymentStatus(determineTargetStatus(template.getPaymentStatus(), fee.getPaymentStatus()));

            BigDecimal share = baseShare;
            if (remainder.compareTo(unit) >= 0) {
                share = share.add(unit);
                remainder = remainder.subtract(unit);
            } else if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                share = share.add(remainder);
                remainder = BigDecimal.ZERO;
            }
            fee.setAmount(share);
            fee.setTotalAmount(totalAmount);
            toPersist.add(fee);
        }

        feeRepository.saveAll(toPersist);
    }

    private Contract getRequiredContract(Fee fee) {
        if (fee.getContract() == null || fee.getContract().getId() == null) {
            throw new IllegalArgumentException("Cần chọn hợp đồng để tạo phí.");
        }
        return contractRepository.findById(fee.getContract().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng với ID đã chọn."));
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount;
    }

    private List<Fee> buildMonthlyFees(Contract contract,
                                       Map<LocalDate, Fee> existingByDueDate,
                                       List<RoomTypePriceHistory> priceHistory) {
        List<Fee> fees = new ArrayList<>();
        YearMonth cursor = YearMonth.from(contract.getStartDate());
        YearMonth end = YearMonth.from(contract.getEndDate());
        while (!cursor.isAfter(end)) {
            LocalDate dueDate = determineDueDateForMonth(cursor, contract);
            Fee fee = existingByDueDate.remove(dueDate);
            if (fee == null) {
                fee = new Fee();
            }
            BigDecimal amount = resolveMonthlyAmount(contract, dueDate, priceHistory);
            populateRentFee(contract, fee, dueDate, amount);
            fees.add(fee);
            cursor = cursor.plusMonths(1);
        }
        return fees;
    }

    private Fee buildFullTermFee(Contract contract,
                                 Map<LocalDate, Fee> existingByDueDate,
                                 List<RoomTypePriceHistory> priceHistory) {
        YearMonth start = YearMonth.from(contract.getStartDate());
        YearMonth end = YearMonth.from(contract.getEndDate());
        YearMonth cursor = start;
        BigDecimal total = BigDecimal.ZERO;
        while (!cursor.isAfter(end)) {
            LocalDate dueDate = determineDueDateForMonth(cursor, contract);
            total = total.add(resolveMonthlyAmount(contract, dueDate, priceHistory));
            cursor = cursor.plusMonths(1);
        }
        LocalDate dueDate = determineDueDateForMonth(start, contract);
        Fee fee = existingByDueDate.remove(dueDate);
        if (fee == null) {
            fee = new Fee();
        }
        populateRentFee(contract, fee, dueDate, total);
        fee.setTotalAmount(total);
        return fee;
    }

    private void populateRentFee(Contract contract, Fee fee, LocalDate dueDate, BigDecimal amount) {
        fee.setContract(contract);
        fee.setScope(FeeScope.INDIVIDUAL);
        fee.setGroupCode(null);
        fee.setType(FeeType.RENT);
        fee.setDueDate(dueDate);
        fee.setPaymentStatus(determineTargetStatus(null, fee.getPaymentStatus()));
        fee.setAmount(amount);
        fee.setTotalAmount(amount);
    }

    private BigDecimal resolveMonthlyAmount(Contract contract, LocalDate dueDate, List<RoomTypePriceHistory> priceHistory) {
        int price = resolvePriceForDate(contract.getRoom(), dueDate, priceHistory);
        Room room = contract.getRoom();
        if (room == null) {
            return BigDecimal.valueOf(price);
        }
        List<Contract> occupants = findActiveRoomContracts(room.getId(), dueDate);
        if (occupants.isEmpty()) {
            return BigDecimal.valueOf(price);
        }
        int occupantCount = occupants.size();
        int baseShare = price / occupantCount;
        int remainder = price - (baseShare * occupantCount);
        int position = 0;
        for (int i = 0; i < occupants.size(); i++) {
            if (Objects.equals(occupants.get(i).getId(), contract.getId())) {
                position = i;
                break;
            }
        }
        int share = baseShare;
        if (remainder > 0 && position < remainder) {
            share += 1;
        }
        return BigDecimal.valueOf(Math.max(share, 0));
    }

    private LocalDate determineDueDateForMonth(YearMonth month, Contract contract) {
        int preferredDay = contract.getBillingDayOfMonth() != null
                ? contract.getBillingDayOfMonth()
                : contract.getStartDate().getDayOfMonth();
        int day = Math.max(1, Math.min(preferredDay, month.lengthOfMonth()));
        LocalDate candidate = month.atDay(day);
        if (candidate.isBefore(contract.getStartDate())) {
            candidate = contract.getStartDate();
        }
        if (candidate.isAfter(contract.getEndDate())) {
            candidate = contract.getEndDate();
        }
        return candidate;
    }

    private List<RoomTypePriceHistory> resolvePriceHistory(Room room) {
        if (room == null || room.getRoomType() == null || room.getRoomType().getId() == null) {
            return List.of();
        }
        return roomTypePriceHistoryRepository.findByRoomType_IdOrderByEffectiveFromAsc(room.getRoomType().getId());
    }

    private int resolvePriceForDate(Room room, LocalDate date, List<RoomTypePriceHistory> priceHistory) {
        int fallback = room != null ? room.getPrice() : 0;
        RoomType roomType = room != null ? room.getRoomType() : null;
        int resolved = fallback;
        for (RoomTypePriceHistory entry : priceHistory) {
            LocalDate effectiveFrom = entry.getEffectiveFrom();
            if (effectiveFrom == null) {
                continue;
            }
            if (!effectiveFrom.isAfter(date)) {
                resolved = entry.getNewPrice();
            } else {
                break;
            }
        }
        if (resolved <= 0 && roomType != null) {
            resolved = roomType.getCurrentPrice();
        }
        if (resolved <= 0) {
            resolved = fallback;
        }
        return Math.max(resolved, 0);
    }

    private PaymentStatus normalizePaymentStatus(PaymentStatus status) {
        return status != null ? status : PaymentStatus.UNPAID;
    }

    private PaymentStatus determineTargetStatus(PaymentStatus requested, PaymentStatus existing) {
        return requested != null ? requested : normalizePaymentStatus(existing);
    }

    private Contract findContractIfPresent(Fee fee) {
        if (fee.getContract() == null || fee.getContract().getId() == null) {
            return null;
        }
        return contractRepository.findById(fee.getContract().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng với ID đã chọn."));
    }

    private Long resolveRoomId(Long requestedRoomId, Contract baseContract) {
        if (requestedRoomId != null) {
            return requestedRoomId;
        }
        if (baseContract != null && baseContract.getRoom() != null) {
            return baseContract.getRoom().getId();
        }
        throw new IllegalArgumentException("Cần chọn phòng để phân bổ phí cho cả phòng.");
    }

    private List<Contract> findActiveRoomContracts(Long roomId, LocalDate referenceDate) {
        if (roomId == null) {
            return List.of();
        }
        LocalDate date = referenceDate != null ? referenceDate : LocalDate.now();
        return contractRepository.findByRoom_Id(roomId).stream()
                .filter(contract -> isEligibleForRoomFee(contract, date))
                .sorted(Comparator
                        .comparing(Contract::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Contract::getId))
                .toList();
    }

    private boolean isEligibleForRoomFee(Contract contract, LocalDate referenceDate) {
        if (contract == null || contract.getStudent() == null) {
            return false;
        }
        String status = contract.getStatus();
        if (status != null) {
            String normalized = status.trim().toUpperCase();
            if (INACTIVE_CONTRACT_STATUSES.contains(normalized)) {
                return false;
            }
        }
        LocalDate start = contract.getStartDate();
        LocalDate end = contract.getEndDate();
        LocalDate date = referenceDate != null ? referenceDate : LocalDate.now();
        if (start != null && start.isAfter(date)) {
            return false;
        }
        if (end != null && end.isBefore(date)) {
            return false;
        }
        return true;
    }
}
