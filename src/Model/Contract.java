package Model;

public class Contract {
    private int contract_id;
    private String startDate;
    private String endDate;
    private double room_price;
    private String paymentMethod;
    private Student student;
    private Room room;

    public Contract(int contract_id) {
        this.contract_id = contract_id;
    }

    public Contract(int contract_id, String startDate, String endDate, double room_price, String paymentMethod, Student student, Room room) {
        this.contract_id = contract_id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.room_price = room_price;
        this.paymentMethod = paymentMethod;
        this.student = student;
        this.room = room;
    }

    public Contract() {

    }

    // Getter and Setter methods
    public int getcontract_id() {
        return contract_id;
    }

    public void setcontract_id(int contract_id) {
        this.contract_id = contract_id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getroom_price() {
        return room_price;
    }

    public void setroom_price(double room_price) {
        this.room_price = room_price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}