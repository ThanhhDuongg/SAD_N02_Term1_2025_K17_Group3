package com.example.dorm.model;

public enum RoleName {
    ROLE_ADMIN("Quản lý KTX"),
    ROLE_STAFF("Nhân viên hỗ trợ"),
    ROLE_STUDENT("Sinh viên");

    private final String displayName;

    RoleName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
