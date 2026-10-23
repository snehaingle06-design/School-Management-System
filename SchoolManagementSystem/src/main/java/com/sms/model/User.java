package com.sms.model;

import java.sql.Timestamp;

/**
 * Represents a system user with authentication credentials and role.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role; // ADMIN, TEACHER, STUDENT
    private Integer linkedId;
    private boolean isActive;
    private Timestamp lastLogin;
    private Timestamp createdAt;

    public User() {}

    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getLinkedId() { return linkedId; }
    public void setLinkedId(Integer linkedId) { this.linkedId = linkedId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}

