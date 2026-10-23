package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.SchoolClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public SchoolClass findById(int id) {
        String sql = "SELECT * FROM classes WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractClass(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding class by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<SchoolClass> findAll() {
        List<SchoolClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY class_name, section";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    classes.add(extractClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all classes: " + e.getMessage());
            e.printStackTrace();
        }
        return classes;
    }

    public boolean insert(SchoolClass schoolClass) {
        String sql = "INSERT INTO classes (class_name, section, room_number) VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, schoolClass.getClassName());
                pstmt.setString(2, schoolClass.getSection());
                pstmt.setString(3, schoolClass.getRoomNumber());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            schoolClass.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting class: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SchoolClass schoolClass) {
        String sql = "UPDATE classes SET class_name = ?, section = ?, room_number = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, schoolClass.getClassName());
                pstmt.setString(2, schoolClass.getSection());
                pstmt.setString(3, schoolClass.getRoomNumber());
                pstmt.setInt(4, schoolClass.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating class: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM classes WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting class: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM classes";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting class count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private SchoolClass extractClass(ResultSet rs) throws SQLException {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(rs.getInt("id"));
        schoolClass.setClassName(rs.getString("class_name"));
        schoolClass.setSection(rs.getString("section"));
        schoolClass.setRoomNumber(rs.getString("room_number"));
        return schoolClass;
    }
}
