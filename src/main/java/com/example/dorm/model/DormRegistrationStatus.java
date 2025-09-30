package com.example.dorm.model;

public enum DormRegistrationStatus {
    PENDING("Đang chờ duyệt"),
    NEEDS_UPDATE("Cần bổ sung"),
    APPROVED("Đã chấp nhận"),
    REJECTED("Từ chối");

    private final String displayName;

    DormRegistrationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
