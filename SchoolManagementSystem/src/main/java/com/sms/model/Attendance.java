package com.sms.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents an attendance record for a student.
 */
public class Attendance {
    private int id;
    private int studentId;
    private int classId;
    private Date attendanceDate;
    private String status; // PRESENT, ABSENT, LEAVE
    private String remarks;
    private Integer markedBy; // teacher id
    private Timestamp createdAt;

    // Transient display fields
    private String studentName;
    private String className;
    private String teacherName;

    public Attendance() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getMarkedBy() { return markedBy; }
    public void setMarkedBy(Integer markedBy) { this.markedBy = markedBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    @Override
    public String toString() {
        return "Attendance{" + studentId + ", " + attendanceDate + ", " + status + "}";
    }
}

