package Model;

public class Fee {
    private String feeId;
    private String studentId;
    private double electricityFee;
    private double waterFee;
    private double cleaningFee;
    private double otherFee;

    public Fee(String feeId, String studentId, double electricityFee, double waterFee, double cleaningFee, double otherFee) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.electricityFee = electricityFee;
        this.waterFee = waterFee;
        this.cleaningFee = cleaningFee;
        this.otherFee = otherFee;
    }

    public double calculateTotalFee() {
        return electricityFee + waterFee + cleaningFee + otherFee;
    }

    // Getter methods
    public String getFeeId() {
        return feeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getElectricityFee() {
        return electricityFee;
    }

    public double getWaterFee() {
        return waterFee;
    }

    public double getCleaningFee() {
        return cleaningFee;
    }

    public double getOtherFee() {
        return otherFee;
    }
}
