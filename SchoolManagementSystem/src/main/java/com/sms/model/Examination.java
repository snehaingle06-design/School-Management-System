package com.sms.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents an examination.
 */
public class Examination {
    private int id;
    private String examName;
    private String examType; // UNIT_TEST, MID_TERM, FINAL, PRACTICE
    private int classId;
    private Date startDate;
    private Date endDate;
    private String academicYear;
    private Timestamp createdAt;

    // Transient
    private String className;

    public Examination() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    @Override
    public String toString() {
        return examName + " (" + examType + ")";
    }
}

