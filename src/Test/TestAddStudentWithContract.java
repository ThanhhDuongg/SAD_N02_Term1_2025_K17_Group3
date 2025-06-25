// File được thực hiện trong quá trình học hiện tại có sự thay đổi nên không chạy được
package Test;

import Model.Room;
import Model.Student;
import service.StudentService;

public class TestAddStudentWithContract {
    public static void main(String[] args) {
        StudentService studentService = new StudentService();

        // Tạo đối tượng phòng (giả định đã có phòng này trong DB với room_id = 1)
        Room room = new Room();
        room.setRoomId(1);  // Đảm bảo phòng này tồn tại trong DB
        room.setRoomNumber("A101");
        room.setRoomType("Phòng đơn");
        room.setroom_price(123.45); // ✅ Tên đúng theo class Room


        // Tạo đối tượng sinh viên mới
        Student student = new Student();
        student.setStudentId(1001);  // Chọn ID chưa tồn tại trong DB
        student.setFullName("Nguyễn Văn A");
        student.setDateOfBirth("2005-08-15");
        student.setGender("Nam");
        student.setAddress("Hà Nội");
        student.setPhoneNumber("0123456789");
        student.setEmail("vana@example.com");
        student.setRoom(room);

        // Thêm sinh viên kèm hợp đồng
        boolean result = studentService.addStudentWithContract(student);

        if (result) {
            System.out.println("Test passed: Thêm sinh viên và hợp đồng thành công.");
        } else {
            System.out.println("Test failed: Không thể thêm sinh viên hoặc hợp đồng.");
        }
    }
}
