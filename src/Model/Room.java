package Model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private int max_occupancy;
    private double room_price;
    private boolean isOccupied;

    public Room() {

    }
    public Room(int roomId) {
        this.roomId = roomId;
    }

    public Room(int roomId, String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.max_occupancy = max_occupancy;
        this.isOccupied = isOccupied;
        this.room_price = room_price;
    }

    public Room(String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.max_occupancy = max_occupancy;
        this.isOccupied = isOccupied;
        this.room_price = room_price;
    }


    // Getter and Setter methods
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getmax_occupancy() {
        return max_occupancy;
    }

    public void setmax_occupancy(int max_occupancy) {
        this.max_occupancy = max_occupancy;
    }

    public double getroom_price() {
        return room_price;
    }

    public void setroom_price(double room_price) {
        this.room_price = room_price;
    }

    public boolean getOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}