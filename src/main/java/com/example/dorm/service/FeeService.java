package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeScope;
import com.example.dorm.model.FeeType;
import com.example.dorm.model.PaymentStatus;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.FeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private final FeeRepository feeRepository;
    private final ContractRepository contractRepository;

    public FeeService(FeeRepository feeRepository, ContractRepository contractRepository) {
        this.feeRepository = feeRepository;
        this.contractRepository = contractRepository;
    }

    public Page<Fee> getAllFees(Pageable pageable) {
        return feeRepository.findAll(pageable);
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
            distributeRoomFee(fee, normalizeAmount(totalAmount));
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
            updateRoomFeeGroup(existing, fee, normalizedTotal);
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
        return feeRepository.findByContract_Student_IdAndPaymentStatus(studentId, PaymentStatus.UNPAID);
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

    private void persistIndividualFee(Fee fee, BigDecimal totalAmount) {
        Contract contract = getRequiredContract(fee);
        fee.setContract(contract);
        fee.setScope(FeeScope.INDIVIDUAL);
        fee.setAmount(totalAmount);
        fee.setTotalAmount(totalAmount);
        fee.setGroupCode(null);
        feeRepository.save(fee);
    }

    private void distributeRoomFee(Fee template, BigDecimal totalAmount) {
        Contract baseContract = getRequiredContract(template);
        List<Contract> roomContracts = contractRepository.findByRoom_Id(baseContract.getRoom().getId());
        if (roomContracts.isEmpty()) {
            throw new IllegalStateException("Phòng chưa có thành viên để phân bổ phí.");
        }

        String groupCode = template.getGroupCode();
        if (groupCode == null || groupCode.isBlank()) {
            groupCode = UUID.randomUUID().toString();
        }

        Map<Long, Fee> existing = Collections.emptyMap();
        distributeToContracts(template, totalAmount, groupCode, roomContracts, existing);
    }

    private void updateRoomFeeGroup(Fee existing, Fee template, BigDecimal totalAmount) {
        String groupCode = existing.getGroupCode();
        if (groupCode == null || groupCode.isBlank()) {
            groupCode = UUID.randomUUID().toString();
        }

        List<Fee> groupMembers = feeRepository.findByGroupCode(groupCode);
        if (groupMembers.isEmpty()) {
            groupMembers = List.of(existing);
        }

        Contract baseContract = existing.getContract();
        if (template.getContract() != null && template.getContract().getId() != null) {
            baseContract = getRequiredContract(template);
        }
        List<Contract> roomContracts = contractRepository.findByRoom_Id(baseContract.getRoom().getId());
        if (roomContracts.isEmpty()) {
            throw new IllegalStateException("Phòng chưa có thành viên để phân bổ phí.");
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
        existing.setPaymentStatus(template.getPaymentStatus());
        existing.setAmount(totalAmount);
        existing.setTotalAmount(totalAmount);
        feeRepository.save(existing);
    }

    private void convertIndividualFeeToRoom(Fee existing, Fee template, BigDecimal totalAmount) {
        feeRepository.delete(existing);
        distributeRoomFee(template, totalAmount);
    }

    private void updateIndividual(Fee existing, Fee template, BigDecimal totalAmount) {
        Contract contract = getRequiredContract(template);
        existing.setContract(contract);
        existing.setScope(FeeScope.INDIVIDUAL);
        existing.setGroupCode(null);
        existing.setType(template.getType());
        existing.setDueDate(template.getDueDate());
        existing.setPaymentStatus(template.getPaymentStatus());
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
            fee.setPaymentStatus(template.getPaymentStatus());

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
}
