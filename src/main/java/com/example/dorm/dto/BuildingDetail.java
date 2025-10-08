package com.example.dorm.dto;

import com.example.dorm.model.Building;

import java.util.List;

public record BuildingDetail(Long id,
                             String code,
                             String name,
                             String address,
                             String description,
                             Integer totalFloors,
                             int roomCount,
                             long capacity,
                             long occupiedBeds,
                             List<BuildingDetailRoom> rooms) {

    public BuildingDetail(Building building,
                          int roomCount,
                          long capacity,
                          long occupiedBeds,
                          List<BuildingDetailRoom> rooms) {
        this(building.getId(),
                building.getCode(),
                building.getName(),
                building.getAddress(),
                building.getDescription(),
                building.getTotalFloors(),
                roomCount,
                capacity,
                occupiedBeds,
                rooms);
    }

    public double occupancyRate() {
        if (capacity <= 0) {
            return 0.0;
        }
        return (double) occupiedBeds / capacity * 100.0;
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
