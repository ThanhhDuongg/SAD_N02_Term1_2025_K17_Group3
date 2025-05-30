package Test;

import Model.Student;
import Model.Room;
import Model.Contract;

public class TestStudent {
    public static void main(String[] args) {
        // Tạo Room giả lập
        Room room = new Room(201, "B102", "Deluxe", 3, false, 450.0);

        // Tạo Contract giả lập
        Contract contract = new Contract(
                10,
                "2025-06-01",
                "2026-06-01",
                450.0,
                "Tiền mặt",
                null,    // sẽ gán Student sau
                room
        );

        // Tạo Student và gán Room, Contract
        Student student = new Student();
        student.setStudentId(1001);
        student.setFullName("Trần Thị B");
        student.setDateOfBirth("2003-07-15");
        student.setGender("Nữ");
        student.setAddress("456 Nguyễn Văn Cừ, Q.5");
        student.setPhoneNumber("0912345678");
        student.setEmail("b@student.edu.vn");
        student.setRoom(room);
        student.setContract(contract);

        // Gán lại student cho contract (liên kết 2 chiều)
        contract.setStudent(student);

        // In thông tin
        System.out.println("===== Thông tin sinh viên =====");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("Tên: " + student.getFullName());
        System.out.println("Ngày sinh: " + student.getDateOfBirth());
        System.out.println("Giới tính: " + student.getGender());
        System.out.println("Địa chỉ: " + student.getAddress());
        System.out.println("SĐT: " + student.getPhoneNumber());
        System.out.println("Email: " + student.getEmail());

        System.out.println("\n=== Thông tin phòng ===");
        System.out.println("Phòng số: " + student.getRoom().getRoomNumber());
        System.out.println("Loại phòng: " + student.getRoom().getRoomType());
        System.out.println("Giá: " + student.getRoom().getroom_price());

        System.out.println("\n=== Hợp đồng ===");
        System.out.println("Mã HĐ: " + student.getContract().getcontract_id());
        System.out.println("Ngày bắt đầu: " + student.getContract().getStartDate());
        System.out.println("Ngày kết thúc: " + student.getContract().getEndDate());
        System.out.println("Phương thức thanh toán: " + student.getContract().getPaymentMethod());
    }
}
