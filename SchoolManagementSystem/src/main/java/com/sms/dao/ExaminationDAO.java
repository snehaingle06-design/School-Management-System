package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.Examination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExaminationDAO {

    public Examination findById(int id) {
        String sql = "SELECT e.*, c.class_name FROM examinations e LEFT JOIN classes c ON e.class_id = c.id WHERE e.id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractExamination(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding examination by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Examination> findAll() {
        List<Examination> examinations = new ArrayList<>();
        String sql = "SELECT e.*, c.class_name FROM examinations e LEFT JOIN classes c ON e.class_id = c.id ORDER BY e.exam_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    examinations.add(extractExamination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all examinations: " + e.getMessage());
            e.printStackTrace();
        }
        return examinations;
    }

    public List<Examination> findByClassId(int classId) {
        List<Examination> examinations = new ArrayList<>();
        String sql = "SELECT e.*, c.class_name FROM examinations e LEFT JOIN classes c ON e.class_id = c.id WHERE e.class_id = ? ORDER BY e.exam_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        examinations.add(extractExamination(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding examinations by class id: " + e.getMessage());
            e.printStackTrace();
        }
        return examinations;
    }

    public boolean insert(Examination exam) {
        String sql = "INSERT INTO examinations (exam_name, class_id, subject_id, exam_date, total_marks) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, exam.getExamName());
                pstmt.setInt(2, exam.getClassId());
                pstmt.setInt(3, exam.getSubjectId());
                pstmt.setDate(4, exam.getExamDate());
                pstmt.setInt(5, exam.getTotalMarks());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            exam.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting examination: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Examination exam) {
        String sql = "UPDATE examinations SET exam_name = ?, class_id = ?, subject_id = ?, exam_date = ?, total_marks = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, exam.getExamName());
                pstmt.setInt(2, exam.getClassId());
                pstmt.setInt(3, exam.getSubjectId());
                pstmt.setDate(4, exam.getExamDate());
                pstmt.setInt(5, exam.getTotalMarks());
                pstmt.setInt(6, exam.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating examination: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM examinations WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting examination: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Examination extractExamination(ResultSet rs) throws SQLException {
        Examination exam = new Examination();
        exam.setId(rs.getInt("id"));
        exam.setExamName(rs.getString("exam_name"));
        exam.setClassId(rs.getInt("class_id"));
        exam.setSubjectId(rs.getInt("subject_id"));
        exam.setExamDate(rs.getDate("exam_date"));
        exam.setTotalMarks(rs.getInt("total_marks"));
        try {
            exam.setClassName(rs.getString("class_name"));
        } catch (SQLException e) {
            // class_name column not present in this query
        }
        return exam;
    }
}
