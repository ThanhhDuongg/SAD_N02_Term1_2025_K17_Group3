package Test;

import Model.Room;
import Model.ARoom;
import Model.BRoom;

public class TestRoom {
    public static void main(String[] args) {
        // === Test Room cơ bản ===
        Room basicRoom = new Room(10, "A101", "Standard", 2, false, 300.0);
        System.out.println("===== Room =====");
        System.out.println("Room ID: " + basicRoom.getRoomId());
        System.out.println("Room Number: " + basicRoom.getRoomNumber());
        System.out.println("Room Type: " + basicRoom.getRoomType());
        System.out.println("Max Occupancy: " + basicRoom.getmax_occupancy());
        System.out.println("Is Occupied: " + basicRoom.getOccupied());
        System.out.println("Room Price: " + basicRoom.getroom_price());

        // === Test ARoom ===
        ARoom aRoom = new ARoom(20, "B202", "VIP", 3, true, 500.0, 100.0);
        System.out.println("\n===== ARoom =====");
        System.out.println("Room ID: " + aRoom.getRoomId());
        System.out.println("Room Number: " + aRoom.getRoomNumber());
        System.out.println("Room Type: " + aRoom.getRoomType());
        System.out.println("Max Occupancy: " + aRoom.getmax_occupancy());
        System.out.println("Is Occupied: " + aRoom.getOccupied());
        System.out.println("Base Price: " + (aRoom.getroom_price() - aRoom.getAdditionalFee()));
        System.out.println("Additional Fee: " + aRoom.getAdditionalFee());
        System.out.println("Total Price: " + aRoom.getroom_price());

        // === Test BRoom ===
        BRoom bRoom = new BRoom(30, "C303", "Economy", 1, false, 200.0);
        System.out.println("\n===== BRoom =====");
        System.out.println("Room ID: " + bRoom.getRoomId());
        System.out.println("Room Number: " + bRoom.getRoomNumber());
        System.out.println("Room Type: " + bRoom.getRoomType());
        System.out.println("Max Occupancy: " + bRoom.getmax_occupancy());
        System.out.println("Is Occupied: " + bRoom.getOccupied());
        System.out.println("Room Price: " + bRoom.getroom_price());
    }
}
