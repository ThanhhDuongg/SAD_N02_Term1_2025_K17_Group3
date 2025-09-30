package com.example.dorm.service;

import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeType;
import com.example.dorm.model.PaymentStatus;
import com.example.dorm.repository.FeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
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

    public Fee createFee(Fee fee) {
        return feeRepository.save(fee);
    }

    public Fee updateFee(Long id, Fee fee) {
        Fee existing = feeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee not found"));
        existing.setAmount(fee.getAmount());
        existing.setContract(fee.getContract());
        existing.setType(fee.getType());
        existing.setDueDate(fee.getDueDate());
        existing.setPaymentStatus(fee.getPaymentStatus());
        return feeRepository.save(existing);
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
}
