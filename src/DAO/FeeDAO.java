package DAO;

import Model.Fee;
import Model.FeeType;
import Model.Student;
import connectionDB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO implements Search {
    public static Connection connection;

    public FeeDAO() {
        connection = DBConnection.getConnection();
    }

    public List<Fee> getAllFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Fee fee = new Fee();
                fee.setFeeId(resultSet.getInt("fee_id"));

                String feeTypeStr = resultSet.getString("fee_type");
                FeeType feeType = FeeType.valueOf(feeTypeStr.toUpperCase());
                fee.setFeeType(feeType);

                fee.setFeeAmount(resultSet.getDouble("amount"));
                fee.setPaymentDate(resultSet.getString("payment_date"));

                Student student = new Student();
                student.setStudentId(resultSet.getInt("student_id"));
                fee.setStudent(student);

                fee.setStatus(resultSet.getString("status")); // ✅ fix: dùng String
                fees.add(fee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid fee type found in database.");
            e.printStackTrace();
        }

        return fees;
    }

    @Override
    public List<Fee> searchByName(String feeTypeName) {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees WHERE fee_type = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            FeeType feeType = FeeType.valueOf(feeTypeName.toUpperCase());
            statement.setString(1, feeType.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Fee fee = new Fee();
                    fee.setFeeId(resultSet.getInt("fee_id"));
                    fee.setFeeType(feeType);
                    fee.setFeeAmount(resultSet.getDouble("amount"));
                    fee.setPaymentDate(resultSet.getString("payment_date"));

                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("student_id"));
                    fee.setStudent(student);

                    fee.setStatus(resultSet.getString("status")); // ✅ fix
                    fees.add(fee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid fee type input: " + feeTypeName);
        }
        return fees;
    }

    public boolean addNewFee(Fee fee) {
        String sql = "INSERT INTO fees (fee_type, amount, payment_date, student_id, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fee.getFeeType().name());
            statement.setDouble(2, fee.getFeeAmount());

            if ("Đã thanh toán".equals(fee.getStatus())) {
                statement.setString(3, fee.getPaymentDate());
            } else {
                statement.setNull(3, java.sql.Types.DATE);
            }

            statement.setInt(4, fee.getStudent().getStudentId());
            statement.setString(5, fee.getStatus());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFee(Fee fee) {
        System.out.println("Accessed updateFee method");

        String sql = "UPDATE fees SET fee_type = ?, amount = ?, payment_date = ?, student_id = ?, status = ? WHERE fee_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("Fee Type: " + fee.getFeeType());
            statement.setString(1, fee.getFeeType().name());

            System.out.println("Fee Amount: " + fee.getFeeAmount());
            statement.setDouble(2, fee.getFeeAmount());

            if ("Đã thanh toán".equals(fee.getStatus())) {
                System.out.println("Payment Date: " + fee.getPaymentDate());
                statement.setString(3, fee.getPaymentDate());
            } else {
                System.out.println("Setting Payment Date to NULL as fee is not paid.");
                statement.setNull(3, java.sql.Types.DATE);
            }

            System.out.println("Student ID: " + fee.getStudent().getStudentId());
            statement.setInt(4, fee.getStudent().getStudentId());

            System.out.println("Status: " + fee.getStatus());
            statement.setString(5, fee.getStatus());

            System.out.println("Fee ID: " + fee.getFeeId());
            statement.setInt(6, fee.getFeeId());

            int rowsUpdated = statement.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFee(int feeId) {
        String sql = "DELETE FROM fees WHERE fee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, feeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
