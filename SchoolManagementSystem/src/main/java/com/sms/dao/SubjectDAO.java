package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public Subject findById(int id) {
        String sql = "SELECT * FROM subjects WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractSubject(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding subject by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Subject findByCode(String code) {
        String sql = "SELECT * FROM subjects WHERE subject_code = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractSubject(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding subject by code: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Subject> findAll() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(extractSubject(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all subjects: " + e.getMessage());
            e.printStackTrace();
        }
        return subjects;
    }

    public boolean insert(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, subject.getSubjectCode());
                pstmt.setString(2, subject.getSubjectName());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            subject.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting subject: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Subject subject) {
        String sql = "UPDATE subjects SET subject_code = ?, subject_name = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, subject.getSubjectCode());
                pstmt.setString(2, subject.getSubjectName());
                pstmt.setInt(3, subject.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating subject: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Subject extractSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setSubjectCode(rs.getString("subject_code"));
        subject.setSubjectName(rs.getString("subject_name"));
        return subject;
    }
}
