package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.FeePayment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeePaymentDAO {

    public boolean insert(FeePayment fp) {
        String sql = "INSERT INTO fee_payments (student_id, fee_structure_id, amount_paid, payment_date, payment_method, receipt_number, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, fp.getStudentId());
                pstmt.setInt(2, fp.getFeeStructureId());
                pstmt.setDouble(3, fp.getAmountPaid());
                pstmt.setDate(4, fp.getPaymentDate());
                pstmt.setString(5, fp.getPaymentMethod());
                pstmt.setString(6, fp.getReceiptNumber());
                pstmt.setString(7, fp.getRemarks());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            fp.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting fee payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<FeePayment> findByStudentId(int studentId) {
        List<FeePayment> payments = new ArrayList<>();
        String sql = "SELECT fp.*, s.student_id AS student_code, s.first_name, s.last_name, " +
                     "fs.fee_type, fs.amount AS fee_amount, fs.academic_year " +
                     "FROM fee_payments fp " +
                     "INNER JOIN students s ON fp.student_id = s.id " +
                     "INNER JOIN fee_structure fs ON fp.fee_structure_id = fs.id " +
                     "WHERE fp.student_id = ? ORDER BY fp.payment_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        FeePayment fp = extractFeePayment(rs);
                        fp.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                        fp.setFeeType(rs.getString("fee_type"));
                        payments.add(fp);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding fee payments by student id: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public List<FeePayment> findAll() {
        List<FeePayment> payments = new ArrayList<>();
        String sql = "SELECT fp.*, s.student_id AS student_code, s.first_name, s.last_name, " +
                     "fs.fee_type, fs.amount AS fee_amount, fs.academic_year " +
                     "FROM fee_payments fp " +
                     "INNER JOIN students s ON fp.student_id = s.id " +
                     "INNER JOIN fee_structure fs ON fp.fee_structure_id = fs.id " +
                     "ORDER BY fp.payment_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FeePayment fp = extractFeePayment(rs);
                    fp.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    fp.setFeeType(rs.getString("fee_type"));
                    payments.add(fp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all fee payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public double getTotalCollected() {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) AS total FROM fee_payments";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total collected: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalPaidByStudent(int studentId) {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) AS total FROM fee_payments WHERE student_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("total");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total paid by student: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public String generateReceiptNumber() {
        String sql = "SELECT receipt_number FROM fee_payments ORDER BY id DESC LIMIT 1";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastReceipt = rs.getString("receipt_number");
                    int num = Integer.parseInt(lastReceipt.replace("RCP-", ""));
                    return String.format("RCP-%04d", num + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating receipt number: " + e.getMessage());
            e.printStackTrace();
        }
        return "RCP-0001";
    }

    private FeePayment extractFeePayment(ResultSet rs) throws SQLException {
        FeePayment fp = new FeePayment();
        fp.setId(rs.getInt("id"));
        fp.setStudentId(rs.getInt("student_id"));
        fp.setFeeStructureId(rs.getInt("fee_structure_id"));
        fp.setAmountPaid(rs.getDouble("amount_paid"));
        fp.setPaymentDate(rs.getDate("payment_date"));
        fp.setPaymentMethod(rs.getString("payment_method"));
        fp.setReceiptNumber(rs.getString("receipt_number"));
        fp.setRemarks(rs.getString("remarks"));
        return fp;
    }
}
