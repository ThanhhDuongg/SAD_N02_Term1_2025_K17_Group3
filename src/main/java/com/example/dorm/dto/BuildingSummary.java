package com.example.dorm.dto;

import com.example.dorm.model.Building;

public record BuildingSummary(Long id,
                               String code,
                               String name,
                               String address,
                               Integer totalFloors,
                               int roomCount,
                               long capacity,
                               long occupiedBeds) {

    public BuildingSummary(Building building, int roomCount, long capacity, long occupiedBeds) {
        this(building.getId(),
                building.getCode(),
                building.getName(),
                building.getAddress(),
                building.getTotalFloors(),
                roomCount,
                capacity,
                occupiedBeds);
    }

    public double occupancyRate() {
        if (capacity <= 0) {
            return 0.0;
        }
        return (double) occupiedBeds / capacity * 100.0;
    }
}
