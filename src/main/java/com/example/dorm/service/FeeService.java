package com.example.dorm.service;

import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeType;
import com.example.dorm.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    public Page<Fee> getAllFees(Pageable pageable) {
        return feeRepository.findAll(pageable);
    }

    public Optional<Fee> getFee(Long id) {
        return feeRepository.findById(id);
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
}