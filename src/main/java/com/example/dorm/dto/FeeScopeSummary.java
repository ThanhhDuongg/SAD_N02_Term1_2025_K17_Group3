package com.example.dorm.dto;

import com.example.dorm.model.FeeScope;

import java.math.BigDecimal;

public record FeeScopeSummary(FeeScope scope,
                              long feeCount,
                              BigDecimal totalAmount,
                              BigDecimal unpaidAmount) {

    public String displayName() {
        return scope == FeeScope.ROOM ? "Cả phòng" : "Cá nhân";
    }
}
