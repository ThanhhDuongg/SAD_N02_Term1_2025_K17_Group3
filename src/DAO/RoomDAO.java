package DAO;

import Model.Contract;
import Model.Room;
import Model.Student;
import connectionDB.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements Search{
    private static Connection connection;

    public RoomDAO() {
        connection = DBConnection.getConnection();
    }


    public boolean addRoom(Room room){
        String query = "INSERT INTO rooms (room_id, room_number, room_type, max_occupancy, is_occupied, room_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, room.getRoomId());
            stmt.setString(2, room.getRoomNumber());
            stmt.setString(3, room.getRoomType());
            stmt.setInt(4, room.getmax_occupancy());
            stmt.setInt(5, 0);
            stmt.setDouble(6, room.getroom_price());
            int result = stmt.executeUpdate();
            return result > 0;  // Trả về true nếu thêm thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Room getRoomByRoom_Number(String roomNumber){
        Room room = null;
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    room = new Room(
                            rs.getInt("room_id"),          // Mã phòng
                            rs.getString("room_number"),     // Tên phòng
                            rs.getString("room_type"),
                            rs.getInt("max_occupancy"),         // Sức chứ
                            rs.getBoolean("is_occupied"),// Giá phòng
                            rs.getDouble("room_price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    public List<Room> getAllRooms() {
        String query = "SELECT * FROM rooms";
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setmax_occupancy(rs.getInt("max_occupancy"));
                room.setroom_price(rs.getDouble("room_price"));
                room.setOccupied(rs.getBoolean("is_occupied"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public void updateRoomStatus(int roomId, boolean isOccupied) throws SQLException {
        String sql = "UPDATE rooms SET is_occupied = ? WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isOccupied);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Room> searchByName(String name) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getInt("max_occupancy"),
                            rs.getBoolean("is_occupied"),
                            rs.getDouble("room_price")
                    );
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET room_number = ?, room_type = ?, max_occupancy = ?, room_price = ?, is_occupied = ? WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setInt(3, room.getmax_occupancy());
            stmt.setDouble(4, room.getroom_price());
            stmt.setBoolean(5, room.getOccupied());
            stmt.setInt(6, room.getRoomId());  // Giả sử sinh viên đã có phòng
            int result = stmt.executeUpdate();
            return result > 0;  // Trả về true nếu cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int roomId) {
        String deleteSQL = "DELETE FROM rooms WHERE room_id = ?"; // Câu lệnh SQL xóa phòng

        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, roomId); // Gán roomId vào câu lệnh SQL

            int rowsAffected = stmt.executeUpdate(); // Thực thi câu lệnh SQL
            return rowsAffected > 0; // Trả về true nếu xóa thành công (số dòng bị ảnh hưởng > 0)
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }
}