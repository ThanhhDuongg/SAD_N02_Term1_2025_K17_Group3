package Model;

import java.time.LocalDate;

public class Contract {
    private String contractId;
    private String studentId;
    private String roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentMethod;
    private boolean isActive;

    public Contract(String contractId, String studentId, String roomNumber,
                    LocalDate startDate, LocalDate endDate, String paymentMethod) {
        this.contractId = contractId;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentMethod = paymentMethod;
        this.isActive = true;
    }

    public void cancelContract() {
        this.isActive = false;
    }

    // Getter methods
    public String getContractId() {
        return contractId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isActive() {
        return isActive;
    }
}

