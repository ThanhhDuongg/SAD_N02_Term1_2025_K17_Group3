package Model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomNumber;
    private String buildingNumber;
    private String roomType;
    private double roomPrice;
    private int maxOccupancy;
    private int currentOccupancy;
    private List<Student> students;

    public Room(String roomNumber, String buildingNumber, String roomType, double roomPrice, int maxOccupancy) {
        this.roomNumber = roomNumber;
        this.buildingNumber = buildingNumber;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.maxOccupancy = maxOccupancy;
        this.currentOccupancy = 0;
        this.students = new ArrayList<>();
    }

    // Getters and Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }        

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public List<Student> getStudents() {
        return students;
    }

    // Add a student to the room
    public boolean addStudent(Student student) {
        if (currentOccupancy < maxOccupancy) {
            students.add(student);
            currentOccupancy++;
            return true; // Student successfully added
        } else {
            return false; // Room is full
        }
    }

    // Remove a student from the room
    public boolean removeStudent(Student student) {
        if (students.remove(student)) {
            currentOccupancy--;
            return true; // Student successfully removed
        } else {
            return false; // Student not found in the room
        }
    }

    // Check if the room is full
    public boolean isFull() {
        return currentOccupancy >= maxOccupancy;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomPrice='" + roomPrice + '\'' +
                ", maxOccupancy=" + maxOccupancy +
                ", currentOccupancy=" + currentOccupancy +
                ", students=" + students +
                '}';
    }
}
