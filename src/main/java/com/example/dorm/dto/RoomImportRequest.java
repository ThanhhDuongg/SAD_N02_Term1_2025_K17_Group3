package com.example.dorm.dto;

import com.example.dorm.model.RoomOccupancyStatus;

public record RoomImportRequest(
        String number,
        Long buildingId,
        Integer floor,
        Integer capacity,
        RoomOccupancyStatus occupancyStatus,
        Long roomTypeId
) {
}
