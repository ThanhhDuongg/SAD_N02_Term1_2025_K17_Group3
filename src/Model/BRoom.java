package Model;

public class BRoom extends Room{

    public BRoom(int roomId){
        super(roomId);
    }
    public BRoom(String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price) {
        super(roomNumber, roomType, max_occupancy, isOccupied, room_price);
    }
    public BRoom(int roomId, String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price) {
        super(roomId, roomNumber, roomType, max_occupancy, isOccupied, room_price);
    }
}