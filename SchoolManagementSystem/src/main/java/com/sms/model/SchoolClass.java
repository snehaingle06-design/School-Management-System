package com.sms.model;

import java.sql.Timestamp;

/**
 * Represents a class/section in the school.
 */
public class SchoolClass {
    private int id;
    private String className;
    private String division;
    private String roomNumber;
    private int capacity;
    private Timestamp createdAt;

    public SchoolClass() {}

    public SchoolClass(String className, String division) {
        this.className = className;
        this.division = division;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getDisplayName() {
        return className + (division != null && !division.isEmpty() ? " - " + division : "");
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}

