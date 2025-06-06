package DAO;

import Model.Contract;
import Model.Room;
import Model.Student;
import connectionDB.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements Search {
    public static Connection connection;

    public StudentDAO() {
        connection = DBConnection.getConnection();
    }

    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (student_id, full_name, date_of_birth, gender, address, phone_number, email, room_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, student.getStudentId());
            stmt.setString(2, student.getFullName());
            stmt.setDate(3, Date.valueOf(student.getDateOfBirth()));
            stmt.setString(4, student.getGender());
            stmt.setString(5, student.getAddress());
            stmt.setString(6, student.getPhoneNumber());
            stmt.setString(7, student.getEmail());
            stmt.setInt(8, student.getRoom().getRoomId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET full_name = ?, date_of_birth = ?, gender = ?, address = ?, phone_number = ?, email = ?, room_id = ? WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, student.getFullName());
            stmt.setDate(2, Date.valueOf(student.getDateOfBirth()));
            stmt.setString(3, student.getGender());
            stmt.setString(4, student.getAddress());
            stmt.setString(5, student.getPhoneNumber());
            stmt.setString(6, student.getEmail());
            stmt.setInt(7, student.getRoom().getRoomId());
            stmt.setInt(8, student.getStudentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error while updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // SỬA LẠI - getStudentById() với JOIN để lấy Contract
    public Student getStudentById(int studentId) {
        String query = """
            SELECT s.student_id, s.full_name, s.date_of_birth, s.gender,
                   s.address, s.phone_number, s.email, s.room_id,
                   r.room_number, r.room_type, r.room_price, r.max_occupancy,
                   r.is_occupied, r.additional_fee,
                   c.contract_id, c.start_date, c.end_date, c.room_price as contract_room_price,
                   c.payment_method, c.status
            FROM students s
            LEFT JOIN rooms r ON s.room_id = r.room_id
            LEFT JOIN contracts c ON s.student_id = c.student_id AND c.status != 'Đã hủy'
            WHERE s.student_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Tạo Room object
                Room room = null;
                if (rs.getObject("room_id") != null) {
                    room = new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getInt("max_occupancy"),
                            rs.getBoolean("is_occupied"),
                            rs.getDouble("room_price")
                    );
                }

                // Tạo Contract object nếu có
                Contract contract = null;
                if (rs.getObject("contract_id") != null) {
                    contract = new Contract(
                            rs.getInt("contract_id"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            rs.getDouble("contract_room_price"),
                            rs.getString("payment_method"),
                            new Student(rs.getInt("student_id")),
                            room
                    );
                }

                // Tạo Student object hoàn chỉnh
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth").toLocalDate().toString(),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        room,
                        contract
                );

                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> searchStudentsByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = """
        SELECT s.student_id, s.full_name, s.date_of_birth, s.gender,
               s.address, s.phone_number, s.email, s.room_id,
               r.room_number, r.room_type, r.room_price, r.max_occupancy,
               r.is_occupied, r.additional_fee,
               c.contract_id, c.start_date, c.end_date, c.room_price as contract_room_price,
               c.payment_method, c.status
        FROM students s
        LEFT JOIN rooms r ON s.room_id = r.room_id
        LEFT JOIN contracts c ON s.student_id = c.student_id AND c.status = 'Đang hiệu lực'
        WHERE s.full_name LIKE ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getInt("max_occupancy"),
                        rs.getBoolean("is_occupied"),
                        rs.getDouble("room_price")
                );

                Contract contract = null;
                if (rs.getInt("contract_id") != 0) {
                    contract = new Contract(
                            rs.getInt("contract_id"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            rs.getDouble("contract_room_price"),
                            rs.getString("payment_method"),
                            new Student(rs.getInt("student_id")),
                            room
                    );
                }

                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("date_of_birth"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        room,
                        contract
                );

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // SỬA LẠI - getAllStudents() với JOIN để lấy Contract
    public List<Student> getAllStudents() {
        String query = """
            SELECT s.student_id, s.full_name, s.date_of_birth, s.gender,
                   s.address, s.phone_number, s.email, s.room_id,
                   r.room_number, r.room_type, r.room_price, r.max_occupancy,
                   r.is_occupied, r.additional_fee,
                   c.contract_id, c.start_date, c.end_date, c.room_price as contract_room_price,
                   c.payment_method, c.status
            FROM students s
            LEFT JOIN rooms r ON s.room_id = r.room_id
            LEFT JOIN contracts c ON s.student_id = c.student_id AND c.status != 'Đã hủy'
            ORDER BY s.student_id
            """;

        List<Student> students = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo Room object
                Room room = null;
                if (rs.getObject("room_id") != null) {
                    room = new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getInt("max_occupancy"),
                            rs.getBoolean("is_occupied"),
                            rs.getDouble("room_price")
                    );
                }

                // Tạo Contract object nếu có
                Contract contract = null;
                if (rs.getObject("contract_id") != null) {
                    contract = new Contract(
                            rs.getInt("contract_id"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            rs.getDouble("contract_room_price"),
                            rs.getString("payment_method"),
                            new Student(rs.getInt("student_id")),
                            room
                    );
                }

                // Tạo Student object hoàn chỉnh
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth").toLocalDate().toString(),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        room,
                        contract
                );

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public List<Student> searchByName(String name) {
        return searchStudentsByName(name);
    }
}