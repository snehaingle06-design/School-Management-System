package com.sms.model;

import java.sql.Timestamp;

/**
 * Represents a subject taught in the school.
 */
public class Subject {
    private int id;
    private String subjectName;
    private String subjectCode;
    private int maxMarks;
    private int passMarks;
    private Timestamp createdAt;

    public Subject() {
        this.maxMarks = 100;
        this.passMarks = 35;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public int getPassMarks() { return passMarks; }
    public void setPassMarks(int passMarks) { this.passMarks = passMarks; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return subjectCode + " - " + subjectName;
    }
}

