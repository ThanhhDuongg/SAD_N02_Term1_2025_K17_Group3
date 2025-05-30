package Model;

public class ARoom extends Room{
    private double additionalFee;

    public ARoom(int roomId){
        super(roomId);
    }
    public ARoom(String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price, double additionalFee) {
        super(roomNumber, roomType, max_occupancy, isOccupied, room_price);
        this.additionalFee = additionalFee;
    }

    public ARoom(int roomId, String roomNumber, String roomType, int max_occupancy, boolean isOccupied, double room_price, double additionalFee) {
        super(roomId, roomNumber, roomType, max_occupancy, isOccupied, room_price);
        this.additionalFee = additionalFee;
    }

    public double getAdditionalFee() {
        return additionalFee;
    }

    public void setAdditionalFee(double additionalFee) {
        this.additionalFee = additionalFee;
    }

    @Override
    public double getroom_price() {
        return super.getroom_price() + additionalFee;
    }
}