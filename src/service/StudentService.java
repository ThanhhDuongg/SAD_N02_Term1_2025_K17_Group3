package service;

import DAO.StudentDAO;
import Model.Student;
import connectionDB.DBConnection;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        studentDAO = new StudentDAO();
    }

    public boolean addStudent(Student student){
        if (student == null || student.getFullName().isEmpty() || student.getEmail().isEmpty()){
            return false;
        }
        return studentDAO.addStudent(student);
    }

    public boolean updateStudent(Student student) {
        if (student == null || student.getStudentId() <= 0) {
            return false;
        }
        return studentDAO.updateStudent(student);
    }

    public Student getStudentById(int studentId) {
        if (studentId <= 0) {
            return null;
        }
        return studentDAO.getStudentById(studentId);
    }

    public List<Student> searchStudentsByName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return null;
        }
        return studentDAO.searchStudentsByName(fullName);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public boolean deleteStudent(int studentId) {
        if (studentId <= 0) {
            return false;
        }
        return studentDAO.deleteStudent(studentId);
    }

    /**
     * Thêm sinh viên kèm theo hợp đồng trong một transaction
     * Không sử dụng contract_id trong bảng students
     */
    public boolean addStudentWithContract(Student student) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm sinh viên
            String insertStudentSQL = "INSERT INTO students (student_id, full_name, date_of_birth, gender, address, phone_number, email, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL);
            studentStmt.setInt(1, student.getStudentId());
            studentStmt.setString(2, student.getFullName());
            studentStmt.setString(3, student.getDateOfBirth());
            studentStmt.setString(4, student.getGender());
            studentStmt.setString(5, student.getAddress());
            studentStmt.setString(6, student.getPhoneNumber());
            studentStmt.setString(7, student.getEmail());
            studentStmt.setInt(8, student.getRoom().getRoomId());
            int studentResult = studentStmt.executeUpdate();

            if (studentResult == 0) {
                throw new SQLException("Thêm sinh viên thất bại");
            }

            // 2. Lấy thông tin phòng để tính giá
            String getRoomPriceSQL = "SELECT room_price FROM rooms WHERE room_id = ?";
            PreparedStatement roomStmt = conn.prepareStatement(getRoomPriceSQL);
            roomStmt.setInt(1, student.getRoom().getRoomId());
            ResultSet roomResult = roomStmt.executeQuery();

            double roomPrice = 0;
            if (roomResult.next()) {
                roomPrice = roomResult.getDouble("room_price");
            } else {
                throw new SQLException("Không tìm thấy thông tin phòng");
            }

            // 3. Thêm hợp đồng
            String insertContractSQL = "INSERT INTO contracts (student_id, room_id, start_date, end_date, payment_method, room_price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement contractStmt = conn.prepareStatement(insertContractSQL);
            contractStmt.setInt(1, student.getStudentId());
            contractStmt.setInt(2, student.getRoom().getRoomId());
            contractStmt.setDate(3, java.sql.Date.valueOf("2024-09-01")); // Năm học mới
            contractStmt.setDate(4, java.sql.Date.valueOf("2025-06-30")); // Kết thúc năm học
            contractStmt.setString(5, "Tiền mặt");
            contractStmt.setDouble(6, roomPrice);
            int contractResult = contractStmt.executeUpdate();

            if (contractResult == 0) {
                throw new SQLException("Thêm hợp đồng thất bại");
            }

            conn.commit();
            JOptionPane.showMessageDialog(null, "Thêm sinh viên và hợp đồng thành công!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy thống kê số sinh viên theo phòng
     */
    public Map<String, Integer> getStudentCountByRoom() {
        Map<String, Integer> roomStatistics = new HashMap<>();
        String sql = "SELECT r.room_number, COUNT(s.student_id) AS student_count " +
                "FROM rooms r " +
                "LEFT JOIN students s ON r.room_id = s.room_id " +
                "GROUP BY r.room_id, r.room_number " +
                "ORDER BY r.room_number";

        try (PreparedStatement statement = studentDAO.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String roomNumber = resultSet.getString("room_number");
                int studentCount = resultSet.getInt("student_count");
                roomStatistics.put(roomNumber, studentCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomStatistics;
    }

    /**
     * Class để lưu thông tin thống kê phòng
     */
    public static class RoomStatistics {
        private String roomId;
        private String roomNumber;
        private String roomType;
        private int studentCount;
        private int totalBeds; // Đổi tên để tương thích với UI
        private boolean isOccupied;

        public RoomStatistics(String roomNumber, String roomType, int studentCount, int totalBeds, boolean isOccupied) {
            this.roomId = roomNumber; // Sử dụng roomNumber làm roomId để hiển thị
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.studentCount = studentCount;
            this.totalBeds = totalBeds;
            this.isOccupied = isOccupied;
        }

        // Getters - bao gồm cả tên cũ và mới để tương thích
        public String getRoomId() { return roomId; }
        public String getRoomNumber() { return roomNumber; }
        public String getRoomType() { return roomType; }
        public int getStudentCount() { return studentCount; }
        public int getTotalBeds() { return totalBeds; }
        public int getMaxOccupancy() { return totalBeds; } // Alias cho tương thích
        public boolean isOccupied() { return isOccupied; }
        public int getAvailableSlots() { return totalBeds - studentCount; }

        @Override
        public String toString() {
            return String.format("Phòng %s (Loại %s): %d/%d sinh viên",
                    roomNumber, roomType, studentCount, totalBeds);
        }
    }

    /**
     * Lấy thống kê chi tiết về phòng
     */
    public List<RoomStatistics> getRoomStatistics() {
        List<RoomStatistics> roomStatisticsList = new ArrayList<>();
        String sql = "SELECT r.room_number, r.room_type, r.max_occupancy, r.is_occupied, " +
                "COUNT(s.student_id) AS student_count " +
                "FROM rooms r " +
                "LEFT JOIN students s ON r.room_id = s.room_id " +
                "GROUP BY r.room_id, r.room_number, r.room_type, r.max_occupancy, r.is_occupied " +
                "ORDER BY r.room_number";

        try (PreparedStatement statement = studentDAO.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String roomNumber = resultSet.getString("room_number");
                String roomType = resultSet.getString("room_type");
                int maxOccupancy = resultSet.getInt("max_occupancy");
                boolean isOccupied = resultSet.getBoolean("is_occupied");
                int studentCount = resultSet.getInt("student_count");

                roomStatisticsList.add(new RoomStatistics(roomNumber, roomType, studentCount, maxOccupancy, isOccupied));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomStatisticsList;
    }

    /**
     * Lấy danh sách sinh viên theo phòng
     */
    public List<Student> getStudentsByRoom(int roomId) {
        if (roomId <= 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT s.*, r.room_number, r.room_type, r.room_price " +
                "FROM students s " +
                "JOIN rooms r ON s.room_id = r.room_id " +
                "WHERE s.room_id = ? " +
                "ORDER BY s.full_name";

        List<Student> students = new ArrayList<>();

        try (PreparedStatement statement = studentDAO.connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Student student = new Student();
                student.setStudentId(resultSet.getInt("student_id"));
                student.setFullName(resultSet.getString("full_name"));
                student.setDateOfBirth(resultSet.getString("date_of_birth"));
                student.setGender(resultSet.getString("gender"));
                student.setAddress(resultSet.getString("address"));
                student.setPhoneNumber(resultSet.getString("phone_number"));
                student.setEmail(resultSet.getString("email"));

                // Tạo Room object nếu cần
                // Room room = new Room();
                // room.setRoomId(resultSet.getInt("room_id"));
                // room.setRoomNumber(resultSet.getString("room_number"));
                // room.setRoomType(resultSet.getString("room_type"));
                // room.setRoomPrice(resultSet.getDouble("room_price"));
                // student.setRoom(room);

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    /**
     * Kiểm tra xem sinh viên có hợp đồng đang hiệu lực không
     */
    public boolean hasActiveContract(int studentId) {
        String sql = "SELECT COUNT(*) FROM contracts WHERE student_id = ? AND status = 'Đang hiệu lực'";

        try (PreparedStatement statement = studentDAO.connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Chuyển sinh viên sang phòng khác
     */
    public boolean transferStudent(int studentId, int newRoomId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật room_id cho sinh viên
            String updateStudentSQL = "UPDATE students SET room_id = ? WHERE student_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateStudentSQL);
            updateStmt.setInt(1, newRoomId);
            updateStmt.setInt(2, studentId);
            int result = updateStmt.executeUpdate();

            if (result == 0) {
                throw new SQLException("Không tìm thấy sinh viên để chuyển phòng");
            }

            // 2. Cập nhật hợp đồng hiện tại (kết thúc hợp đồng cũ)
            String endOldContractSQL = "UPDATE contracts SET status = 'Đã hủy' WHERE student_id = ? AND status = 'Đang hiệu lực'";
            PreparedStatement endContractStmt = conn.prepareStatement(endOldContractSQL);
            endContractStmt.setInt(1, studentId);
            endContractStmt.executeUpdate();

            // 3. Tạo hợp đồng mới
            String getRoomPriceSQL = "SELECT room_price FROM rooms WHERE room_id = ?";
            PreparedStatement roomStmt = conn.prepareStatement(getRoomPriceSQL);
            roomStmt.setInt(1, newRoomId);
            ResultSet roomResult = roomStmt.executeQuery();

            double roomPrice = 0;
            if (roomResult.next()) {
                roomPrice = roomResult.getDouble("room_price");
            }

            String insertNewContractSQL = "INSERT INTO contracts (student_id, room_id, start_date, end_date, payment_method, room_price) VALUES (?, ?, CURDATE(), '2025-06-30', 'Tiền mặt', ?)";
            PreparedStatement newContractStmt = conn.prepareStatement(insertNewContractSQL);
            newContractStmt.setInt(1, studentId);
            newContractStmt.setInt(2, newRoomId);
            newContractStmt.setDouble(3, roomPrice);
            newContractStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}