package Model;

public class Fee {
    private int feeId;
    private FeeType feeType;
    private double feeAmount;
    private String paymentDate;
    private Student student;
    private String status;

    public Fee() {
    }

    public Fee(int feeId) {
        this.feeId = feeId;
    }

    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getStatus() {  // ✅ đúng getter
        return status;
    }

    public void setStatus(String status) {  // ✅ đúng setter
        this.status = status;
    }

    public boolean isPaid() {
        return "Đã thanh toán".equals(this.status);
    }
}
