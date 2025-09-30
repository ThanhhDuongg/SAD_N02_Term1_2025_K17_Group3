package com.example.dorm.dto;

import com.example.dorm.model.FeeType;

import java.math.BigDecimal;

public record FeeTypeSummary(FeeType type,
                             long feeCount,
                             BigDecimal totalAmount,
                             long unpaidCount,
                             BigDecimal unpaidAmount) {

    public String displayName() {
        return switch (type) {
            case RENT -> "Tiền phòng";
            case ELECTRICITY -> "Tiền điện";
            case WATER -> "Tiền nước";
            case MAINTENANCE -> "Bảo trì";
            case CLEANING -> "Dọn dẹp";
        };
    }
}
