package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.FeeStructure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeStructureDAO {

    public FeeStructure findById(int id) {
        String sql = "SELECT fs.*, c.class_name FROM fee_structure fs LEFT JOIN classes c ON fs.class_id = c.id WHERE fs.id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractFeeStructure(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding fee structure by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<FeeStructure> findAll() {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT fs.*, c.class_name FROM fee_structure fs LEFT JOIN classes c ON fs.class_id = c.id ORDER BY c.class_name, fs.fee_type";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    feeStructures.add(extractFeeStructure(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all fee structures: " + e.getMessage());
            e.printStackTrace();
        }
        return feeStructures;
    }

    public List<FeeStructure> findByClassId(int classId) {
        List<FeeStructure> feeStructures = new ArrayList<>();
        String sql = "SELECT fs.*, c.class_name FROM fee_structure fs LEFT JOIN classes c ON fs.class_id = c.id WHERE fs.class_id = ? ORDER BY fs.fee_type";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        feeStructures.add(extractFeeStructure(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding fee structures by class id: " + e.getMessage());
            e.printStackTrace();
        }
        return feeStructures;
    }

    public boolean insert(FeeStructure fs) {
        String sql = "INSERT INTO fee_structure (class_id, fee_type, amount, due_date, academic_year) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, fs.getClassId());
                pstmt.setString(2, fs.getFeeType());
                pstmt.setDouble(3, fs.getAmount());
                pstmt.setDate(4, fs.getDueDate());
                pstmt.setString(5, fs.getAcademicYear());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            fs.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting fee structure: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(FeeStructure fs) {
        String sql = "UPDATE fee_structure SET class_id = ?, fee_type = ?, amount = ?, due_date = ?, academic_year = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, fs.getClassId());
                pstmt.setString(2, fs.getFeeType());
                pstmt.setDouble(3, fs.getAmount());
                pstmt.setDate(4, fs.getDueDate());
                pstmt.setString(5, fs.getAcademicYear());
                pstmt.setInt(6, fs.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating fee structure: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM fee_structure WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting fee structure: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private FeeStructure extractFeeStructure(ResultSet rs) throws SQLException {
        FeeStructure fs = new FeeStructure();
        fs.setId(rs.getInt("id"));
        fs.setClassId(rs.getInt("class_id"));
        fs.setFeeType(rs.getString("fee_type"));
        fs.setAmount(rs.getDouble("amount"));
        fs.setDueDate(rs.getDate("due_date"));
        fs.setAcademicYear(rs.getString("academic_year"));
        try {
            fs.setClassName(rs.getString("class_name"));
        } catch (SQLException e) {
            // class_name column not present in this query
        }
        return fs;
    }
}
