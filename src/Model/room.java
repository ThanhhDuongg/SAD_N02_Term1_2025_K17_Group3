package Model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomNumber;
    private String buildingNumber;
    private String roomType;
    private int maxOccupancy;
    private int currentOccupancy;
    private List<Student> students;

    public Room(String roomNumber, String buildingNumber, String roomType, int maxOccupancy) {
        this.roomNumber = roomNumber;
        this.buildingNumber = buildingNumber;
        this.roomType = roomType;
        this.maxOccupancy = maxOccupancy;
        this.currentOccupancy = 0;
        this.students = new ArrayList<>();
    }
}