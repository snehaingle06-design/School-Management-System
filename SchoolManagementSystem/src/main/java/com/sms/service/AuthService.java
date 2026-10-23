package com.sms.service;

import com.sms.dao.UserDAO;
import com.sms.model.User;
import com.sms.util.PasswordUtil;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service class for authentication and user management operations.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user by username and password.
     * Updates the last login timestamp on successful authentication.
     *
     * @param username the username
     * @param password the plain-text password
     * @return the authenticated User object, or null if authentication fails
     */
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }

        if (PasswordUtil.verifyPassword(password, user.getPassword())) {
            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            userDAO.update(user);
            return user;
        }

        return null;
    }

    /**
     * Changes the password for a given user.
     *
     * @param userId      the user ID
     * @param oldPassword the current plain-text password
     * @param newPassword the new plain-text password
     * @return true if the password was changed successfully
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        return userDAO.update(user);
    }

    /**
     * Creates a new user account.
     *
     * @param username the username
     * @param password the plain-text password (will be hashed)
     * @param role     the user role (e.g., "admin", "teacher", "student")
     * @param linkedId the linked entity ID (student or teacher ID), may be null
     * @return true if the user was created successfully
     */
    public boolean createUser(String username, String password, String role, Integer linkedId) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);
        user.setLinkedId(linkedId != null ? linkedId : 0);
        user.setActive(true);

        return userDAO.insert(user);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the user ID
     * @return true if the user was deleted successfully
     */
    public boolean deleteUser(int userId) {
        return userDAO.delete(userId);
    }

    /**
     * Updates an existing user.
     *
     * @param user the user object with updated fields
     * @return true if the user was updated successfully
     */
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }
        return userDAO.update(user);
    }
}
