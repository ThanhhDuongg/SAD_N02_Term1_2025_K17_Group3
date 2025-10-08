package com.example.dorm.dto;

public record BuildingDetailRoom(Long id,
                                 String number,
                                 String type,
                                 int capacity,
                                 int price,
                                 long occupiedBeds) {

    public double occupancyRate() {
        if (capacity <= 0) {
            return 0.0;
        }
        return (double) occupiedBeds / capacity * 100.0;
    }
}
