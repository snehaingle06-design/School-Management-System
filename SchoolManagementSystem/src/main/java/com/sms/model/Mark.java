package com.sms.model;

import java.sql.Timestamp;

/**
 * Represents marks obtained by a student in an exam for a subject.
 */
public class Mark {
    private int id;
    private int examId;
    private int studentId;
    private int subjectId;
    private double marksObtained;
    private int maxMarks;
    private String grade;
    private String remarks;
    private Timestamp createdAt;

    // Transient display fields
    private String examName;
    private String studentName;
    private String subjectName;

    public Mark() {
        this.maxMarks = 100;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public double getPercentage() {
        return maxMarks > 0 ? (marksObtained / maxMarks) * 100 : 0;
    }

    public boolean isPassed(int passMarks) {
        return marksObtained >= passMarks;
    }

    /**
     * Calculates grade based on percentage.
     */
    public static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 35) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return "Mark{exam=" + examId + ", student=" + studentId + ", subject=" + subjectId + ", marks=" + marksObtained + "}";
    }
}

