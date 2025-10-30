package com.example.dorm.dto;

import com.example.dorm.model.RoomOccupancyStatus;

public record RoomAutoCreateRequest(
        Long buildingId,
        Integer floor,
        Long roomTypeId,
        Integer capacity,
        RoomOccupancyStatus occupancyStatus,
        String prefix,
        Integer startNumber,
        Integer quantity,
        Integer padding
) {
}
