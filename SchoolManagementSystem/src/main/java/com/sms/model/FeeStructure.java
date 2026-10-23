package com.sms.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a fee structure for a class.
 */
public class FeeStructure {
    private int id;
    private int classId;
    private String feeType;
    private double amount;
    private Date dueDate;
    private String academicYear;
    private Timestamp createdAt;

    // Transient
    private String className;

    public FeeStructure() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    @Override
    public String toString() {
        return feeType + " - ₹" + amount;
    }
}

