package com.example.dorm.dto;

import com.example.dorm.model.RoomOccupancyStatus;

public record BuildingDetailRoom(Long id,
                                 String number,
                                 Integer floor,
                                 String type,
                                 int capacity,
                                 int price,
                                 RoomOccupancyStatus occupancyStatus,
                                 long occupiedBeds) {

    public double occupancyRate() {
        if (capacity <= 0) {
            return 0.0;
        }
        return (double) occupiedBeds / capacity * 100.0;
    }

    public String occupancyStatusLabel() {
        return occupancyStatus != null ? occupancyStatus.getDisplayName() : RoomOccupancyStatus.AVAILABLE.getDisplayName();
    }
}
