package com.example.dorm.model;

public enum RoomOccupancyStatus {
    AVAILABLE("Còn chỗ"),
    FULL("Đầy");

    private final String displayName;

    RoomOccupancyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
