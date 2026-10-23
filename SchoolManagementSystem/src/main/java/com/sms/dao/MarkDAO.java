package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.Mark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    public boolean insert(Mark mark) {
        String sql = "INSERT INTO marks (exam_id, student_id, subject_id, marks_obtained, grade, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, mark.getExamId());
                pstmt.setInt(2, mark.getStudentId());
                pstmt.setInt(3, mark.getSubjectId());
                pstmt.setDouble(4, mark.getMarksObtained());
                pstmt.setString(5, mark.getGrade());
                pstmt.setString(6, mark.getRemarks());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            mark.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting mark: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Mark mark) {
        String sql = "UPDATE marks SET marks_obtained = ?, grade = ?, remarks = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, mark.getMarksObtained());
                pstmt.setString(2, mark.getGrade());
                pstmt.setString(3, mark.getRemarks());
                pstmt.setInt(4, mark.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating mark: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Mark findByExamStudentSubject(int examId, int studentId, int subjectId) {
        String sql = "SELECT * FROM marks WHERE exam_id = ? AND student_id = ? AND subject_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, examId);
                pstmt.setInt(2, studentId);
                pstmt.setInt(3, subjectId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractMark(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding mark by exam, student, subject: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Mark> findByExamAndStudent(int examId, int studentId) {
        List<Mark> marks = new ArrayList<>();
        String sql = "SELECT m.*, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "INNER JOIN subjects sub ON m.subject_id = sub.id " +
                     "WHERE m.exam_id = ? AND m.student_id = ? ORDER BY sub.subject_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, examId);
                pstmt.setInt(2, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = extractMark(rs);
                        mark.setSubjectName(rs.getString("subject_name"));
                        marks.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding marks by exam and student: " + e.getMessage());
            e.printStackTrace();
        }
        return marks;
    }

    public List<Mark> findByExamId(int examId) {
        List<Mark> marks = new ArrayList<>();
        String sql = "SELECT m.*, s.student_id AS student_code, s.first_name, s.last_name, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "INNER JOIN students s ON m.student_id = s.id " +
                     "INNER JOIN subjects sub ON m.subject_id = sub.id " +
                     "WHERE m.exam_id = ? ORDER BY s.student_id, sub.subject_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, examId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = extractMark(rs);
                        mark.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                        mark.setSubjectName(rs.getString("subject_name"));
                        marks.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding marks by exam id: " + e.getMessage());
            e.printStackTrace();
        }
        return marks;
    }

    public List<Mark> findByStudentId(int studentId) {
        List<Mark> marks = new ArrayList<>();
        String sql = "SELECT m.*, e.exam_name, e.exam_date, e.total_marks AS exam_total_marks, sub.subject_name, sub.subject_code " +
                     "FROM marks m " +
                     "INNER JOIN examinations e ON m.exam_id = e.id " +
                     "INNER JOIN subjects sub ON m.subject_id = sub.id " +
                     "WHERE m.student_id = ? ORDER BY e.exam_date DESC, sub.subject_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = extractMark(rs);
                        mark.setExamName(rs.getString("exam_name"));
                        mark.setSubjectName(rs.getString("subject_name"));
                        marks.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding marks by student id: " + e.getMessage());
            e.printStackTrace();
        }
        return marks;
    }

    private Mark extractMark(ResultSet rs) throws SQLException {
        Mark mark = new Mark();
        mark.setId(rs.getInt("id"));
        mark.setExamId(rs.getInt("exam_id"));
        mark.setStudentId(rs.getInt("student_id"));
        mark.setSubjectId(rs.getInt("subject_id"));
        mark.setMarksObtained(rs.getDouble("marks_obtained"));
        mark.setGrade(rs.getString("grade"));
        mark.setRemarks(rs.getString("remarks"));
        return mark;
    }
}
