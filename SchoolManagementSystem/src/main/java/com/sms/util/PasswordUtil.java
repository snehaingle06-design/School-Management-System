package com.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing using SHA-256.
 */
public class PasswordUtil {

    /**
     * Hashes a password using SHA-256.
     * @param password the plain-text password
     * @return the hex-encoded hash string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     * @param password the plain-text password to verify
     * @param storedHash the stored hash to compare against
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String hash = hashPassword(password);
        return hash.equals(storedHash);
    }

    // Utility main to generate hashes for sample data
    public static void main(String[] args) {
        System.out.println("admin123 -> " + hashPassword("admin123"));
        System.out.println("teacher123 -> " + hashPassword("teacher123"));
        System.out.println("student123 -> " + hashPassword("student123"));
    }
}

