package Test;

import Model.Contract;
import Model.Room;
import Model.Student;

public class TestContract {
    public static void main(String[] args) {
        // Tạo Room giả lập
        Room room = new Room(301, "C203", "Standard", 2, true, 500.0);

        // Tạo Student giả lập
        Student student = new Student();
        student.setStudentId(2001);
        student.setFullName("Nguyễn Văn C");
        student.setDateOfBirth("2002-12-10");
        student.setGender("Nam");
        student.setAddress("789 Lê Lợi, Q.1");
        student.setPhoneNumber("0909090909");
        student.setEmail("c@student.edu.vn");
        student.setRoom(room);  // gán phòng

        // Tạo Contract và gán vào Student
        Contract contract = new Contract();
        contract.setcontract_id(20);
        contract.setStartDate("2025-07-01");
        contract.setEndDate("2026-07-01");
        contract.setroom_price(500.0);
        contract.setPaymentMethod("Chuyển khoản");
        contract.setStudent(student);
        contract.setRoom(room);

        // Gán contract cho student (liên kết hai chiều)
        student.setContract(contract);

        // In thông tin contract
        System.out.println("===== HỢP ĐỒNG =====");
        System.out.println("Mã HĐ: " + contract.getcontract_id());
        System.out.println("Ngày bắt đầu: " + contract.getStartDate());
        System.out.println("Ngày kết thúc: " + contract.getEndDate());
        System.out.println("Phương thức thanh toán: " + contract.getPaymentMethod());
        System.out.println("Giá phòng: " + contract.getroom_price());

        System.out.println("\n===== SINH VIÊN =====");
        System.out.println("ID: " + contract.getStudent().getStudentId());
        System.out.println("Tên: " + contract.getStudent().getFullName());
        System.out.println("Ngày sinh: " + contract.getStudent().getDateOfBirth());
        System.out.println("Giới tính: " + contract.getStudent().getGender());
        System.out.println("SĐT: " + contract.getStudent().getPhoneNumber());
        System.out.println("Email: " + contract.getStudent().getEmail());

        System.out.println("\n===== PHÒNG =====");
        System.out.println("Số phòng: " + contract.getRoom().getRoomNumber());
        System.out.println("Loại: " + contract.getRoom().getRoomType());
        System.out.println("Giá: " + contract.getRoom().getroom_price());
    }
}
