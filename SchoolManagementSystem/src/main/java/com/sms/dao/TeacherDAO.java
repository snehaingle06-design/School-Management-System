package com.sms.dao;

import com.sms.database.DatabaseConnection;
import com.sms.model.SchoolClass;
import com.sms.model.Subject;
import com.sms.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public Teacher findById(int id) {
        String sql = "SELECT * FROM teachers WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractTeacher(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Teacher findByTeacherId(String teacherId) {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, teacherId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return extractTeacher(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher by teacher_id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> findAll() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers ORDER BY teacher_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teachers.add(extractTeacher(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all teachers: " + e.getMessage());
            e.printStackTrace();
        }
        return teachers;
    }

    public List<Teacher> search(String keyword) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers WHERE teacher_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? ORDER BY teacher_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String pattern = "%" + keyword + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        teachers.add(extractTeacher(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching teachers: " + e.getMessage());
            e.printStackTrace();
        }
        return teachers;
    }

    public boolean insert(Teacher teacher) {
        String sql = "INSERT INTO teachers (teacher_id, first_name, last_name, date_of_birth, gender, address, phone, email, qualification, hire_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, teacher.getTeacherId());
                pstmt.setString(2, teacher.getFirstName());
                pstmt.setString(3, teacher.getLastName());
                pstmt.setDate(4, teacher.getDateOfBirth());
                pstmt.setString(5, teacher.getGender());
                pstmt.setString(6, teacher.getAddress());
                pstmt.setString(7, teacher.getPhone());
                pstmt.setString(8, teacher.getEmail());
                pstmt.setString(9, teacher.getQualification());
                pstmt.setDate(10, teacher.getHireDate());
                pstmt.setString(11, teacher.getStatus());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            teacher.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Teacher teacher) {
        String sql = "UPDATE teachers SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, address = ?, phone = ?, email = ?, qualification = ?, status = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, teacher.getFirstName());
                pstmt.setString(2, teacher.getLastName());
                pstmt.setDate(3, teacher.getDateOfBirth());
                pstmt.setString(4, teacher.getGender());
                pstmt.setString(5, teacher.getAddress());
                pstmt.setString(6, teacher.getPhone());
                pstmt.setString(7, teacher.getEmail());
                pstmt.setString(8, teacher.getQualification());
                pstmt.setString(9, teacher.getStatus());
                pstmt.setInt(10, teacher.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM teachers WHERE status = 'Active'";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting teacher count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public String generateTeacherId() {
        String sql = "SELECT teacher_id FROM teachers ORDER BY id DESC LIMIT 1";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("teacher_id");
                    int num = Integer.parseInt(lastId.replace("TCH", ""));
                    return String.format("TCH%03d", num + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating teacher id: " + e.getMessage());
            e.printStackTrace();
        }
        return "TCH001";
    }

    public boolean assignSubject(int teacherId, int subjectId) {
        String sql = "INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                pstmt.setInt(2, subjectId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error assigning subject to teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean assignClass(int teacherId, int classId) {
        String sql = "INSERT INTO teacher_classes (teacher_id, class_id) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                pstmt.setInt(2, classId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error assigning class to teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Subject> getAssignedSubjects(int teacherId) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.* FROM subjects s INNER JOIN teacher_subjects ts ON s.id = ts.subject_id WHERE ts.teacher_id = ? ORDER BY s.subject_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Subject subject = new Subject();
                        subject.setId(rs.getInt("id"));
                        subject.setSubjectCode(rs.getString("subject_code"));
                        subject.setSubjectName(rs.getString("subject_name"));
                        subjects.add(subject);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting assigned subjects: " + e.getMessage());
            e.printStackTrace();
        }
        return subjects;
    }

    public List<SchoolClass> getAssignedClasses(int teacherId) {
        List<SchoolClass> classes = new ArrayList<>();
        String sql = "SELECT c.* FROM classes c INNER JOIN teacher_classes tc ON c.id = tc.class_id WHERE tc.teacher_id = ? ORDER BY c.class_name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        SchoolClass sc = new SchoolClass();
                        sc.setId(rs.getInt("id"));
                        sc.setClassName(rs.getString("class_name"));
                        sc.setSection(rs.getString("section"));
                        sc.setRoomNumber(rs.getString("room_number"));
                        classes.add(sc);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting assigned classes: " + e.getMessage());
            e.printStackTrace();
        }
        return classes;
    }

    public void removeAllSubjectAssignments(int teacherId) {
        String sql = "DELETE FROM teacher_subjects WHERE teacher_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error removing subject assignments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeAllClassAssignments(int teacherId) {
        String sql = "DELETE FROM teacher_classes WHERE teacher_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teacherId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error removing class assignments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Teacher extractTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt("id"));
        teacher.setTeacherId(rs.getString("teacher_id"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setDateOfBirth(rs.getDate("date_of_birth"));
        teacher.setGender(rs.getString("gender"));
        teacher.setAddress(rs.getString("address"));
        teacher.setPhone(rs.getString("phone"));
        teacher.setEmail(rs.getString("email"));
        teacher.setQualification(rs.getString("qualification"));
        teacher.setHireDate(rs.getDate("hire_date"));
        teacher.setStatus(rs.getString("status"));
        return teacher;
    }
}
