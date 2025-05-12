package Model;

public class Student {
    private String name;
    private int dateOfBirth;
    private String gender;
    private String studentId;
    private String address;
    private String phoneNumber;
    private String email;
    private String identityCard;
    private String roomNumber;
    public Student(String name, int dateOfBirth, String gender, String studentId, String address, String phoneNumber, String email, String IdentityCard, String roomNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.studentId = studentId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.identityCard = identityCard;
        this.roomNumber = roomNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(int dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getIdentityCard() {
        return IdentityCard;
    }
    public void setIdentityCard(String identityCard) {
        identityCard = identityCard;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    @Override 
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", studentId='" + studentId + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", identityCard='" + identityCard + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }
}  
