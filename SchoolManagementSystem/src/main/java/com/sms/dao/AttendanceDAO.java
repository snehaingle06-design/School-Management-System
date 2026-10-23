package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean insert(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id, class_id, date, status, remarks) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, attendance.getStudentId());
                pstmt.setInt(2, attendance.getClassId());
                pstmt.setDate(3, attendance.getDate());
                pstmt.setString(4, attendance.getStatus());
                pstmt.setString(5, attendance.getRemarks());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            attendance.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting attendance: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Attendance attendance) {
        String sql = "UPDATE attendance SET status = ?, remarks = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, attendance.getStatus());
                pstmt.setString(2, attendance.getRemarks());
                pstmt.setInt(3, attendance.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating attendance: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Attendance> findByStudentId(int studentId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.student_id AS student_code, s.first_name, s.last_name, c.class_name " +
                     "FROM attendance a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "LEFT JOIN classes c ON a.class_id = c.id " +
                     "WHERE a.student_id = ? ORDER BY a.date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Attendance attendance = extractAttendance(rs);
                        attendance.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                        attendance.setClassName(rs.getString("class_name"));
                        attendanceList.add(attendance);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendance by student id: " + e.getMessage());
            e.printStackTrace();
        }
        return attendanceList;
    }

    public List<Attendance> findByClassAndDate(int classId, Date date) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.student_id AS student_code, s.first_name, s.last_name " +
                     "FROM attendance a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "WHERE a.class_id = ? AND a.date = ? ORDER BY s.student_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                pstmt.setDate(2, date);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Attendance attendance = extractAttendance(rs);
                        attendance.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                        attendanceList.add(attendance);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendance by class and date: " + e.getMessage());
            e.printStackTrace();
        }
        return attendanceList;
    }

    public Attendance findByStudentAndDate(int studentId, Date date) {
        String sql = "SELECT * FROM attendance WHERE student_id = ? AND date = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setDate(2, date);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractAttendance(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendance by student and date: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public double getAttendancePercentage(int studentId) {
        String sql = "SELECT COUNT(*) AS total, SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count FROM attendance WHERE student_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int total = rs.getInt("total");
                        int presentCount = rs.getInt("present_count");
                        if (total > 0) {
                            return ((double) presentCount / total) * 100.0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance percentage: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Attendance> getMonthlyAttendance(int classId, int month, int year) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, s.student_id AS student_code, s.first_name, s.last_name " +
                     "FROM attendance a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "WHERE a.class_id = ? AND MONTH(a.date) = ? AND YEAR(a.date) = ? " +
                     "ORDER BY a.date, s.student_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                pstmt.setInt(2, month);
                pstmt.setInt(3, year);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Attendance attendance = extractAttendance(rs);
                        attendance.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                        attendanceList.add(attendance);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly attendance: " + e.getMessage());
            e.printStackTrace();
        }
        return attendanceList;
    }

    private Attendance extractAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getInt("id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setClassId(rs.getInt("class_id"));
        attendance.setDate(rs.getDate("date"));
        attendance.setStatus(rs.getString("status"));
        attendance.setRemarks(rs.getString("remarks"));
        return attendance;
    }
}
