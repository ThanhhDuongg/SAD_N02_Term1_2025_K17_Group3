package com.example.dorm.model;

public enum DormRegistrationPeriodStatus {
    SCHEDULED("Đã lên lịch"),
    OPEN("Đang mở"),
    CLOSED("Đã đóng");

    private final String displayName;

    DormRegistrationPeriodStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
