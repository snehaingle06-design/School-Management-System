package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student findById(int id) {
        String sql = "SELECT s.*, c.class_name FROM students s LEFT JOIN classes c ON s.class_id = c.id WHERE s.id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractStudent(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Student findByStudentId(String studentId) {
        String sql = "SELECT s.*, c.class_name FROM students s LEFT JOIN classes c ON s.class_id = c.id WHERE s.student_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractStudent(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student by student_id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.class_name FROM students s LEFT JOIN classes c ON s.class_id = c.id ORDER BY s.student_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> findByClassId(int classId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.class_name FROM students s LEFT JOIN classes c ON s.class_id = c.id WHERE s.class_id = ? ORDER BY s.student_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        students.add(extractStudent(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding students by class id: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> search(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.class_name FROM students s LEFT JOIN classes c ON s.class_id = c.id " +
                     "WHERE s.student_id LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ? ORDER BY s.student_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String pattern = "%" + keyword + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        students.add(extractStudent(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    public boolean insert(Student student) {
        String sql = "INSERT INTO students (student_id, first_name, last_name, date_of_birth, gender, address, phone, email, class_id, enrollment_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, student.getStudentId());
                pstmt.setString(2, student.getFirstName());
                pstmt.setString(3, student.getLastName());
                pstmt.setDate(4, student.getDateOfBirth());
                pstmt.setString(5, student.getGender());
                pstmt.setString(6, student.getAddress());
                pstmt.setString(7, student.getPhone());
                pstmt.setString(8, student.getEmail());
                pstmt.setInt(9, student.getClassId());
                pstmt.setDate(10, student.getEnrollmentDate());
                pstmt.setString(11, student.getStatus());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            student.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Student student) {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, address = ?, phone = ?, email = ?, class_id = ?, status = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getFirstName());
                pstmt.setString(2, student.getLastName());
                pstmt.setDate(3, student.getDateOfBirth());
                pstmt.setString(4, student.getGender());
                pstmt.setString(5, student.getAddress());
                pstmt.setString(6, student.getPhone());
                pstmt.setString(7, student.getEmail());
                pstmt.setInt(8, student.getClassId());
                pstmt.setString(9, student.getStatus());
                pstmt.setInt(10, student.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM students WHERE status = 'Active'";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public String generateStudentId() {
        String sql = "SELECT student_id FROM students ORDER BY id DESC LIMIT 1";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("student_id");
                    int num = Integer.parseInt(lastId.replace("STU", ""));
                    return String.format("STU%03d", num + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating student id: " + e.getMessage());
            e.printStackTrace();
        }
        return "STU001";
    }

    private Student extractStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setClassId(rs.getInt("class_id"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setStatus(rs.getString("status"));
        try {
            student.setClassName(rs.getString("class_name"));
        } catch (SQLException e) {
            // class_name column not present in this query
        }
        return student;
    }
}
