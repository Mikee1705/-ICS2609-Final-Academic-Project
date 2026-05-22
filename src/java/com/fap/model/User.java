package com.fap.model;

import java.sql.Timestamp;

/**
 * Model: Users table (Derby)
 *
 * Stored in Derby — the original DBMS carried over from MP2–MP4.
 * Used for authentication and the User List PDF report.
 */
public class User {

    public enum Role { Admin, Teacher, Student }

    private int       userId;
    private String    username;
    private String    passwordHash;   // salt:hash — NEVER print this
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    role;           // Admin / Teacher / Student
    private boolean   isActive;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public User() {}

    // Getters & Setters
    public int getUserId()                                { return userId; }
    public void setUserId(int userId)                     { this.userId = userId; }

    public String getUsername()                           { return username; }
    public void setUsername(String username)              { this.username = username; }

    public String getPasswordHash()                       { return passwordHash; }
    public void setPasswordHash(String passwordHash)      { this.passwordHash = passwordHash; }

    public String getFirstName()                          { return firstName; }
    public void setFirstName(String firstName)            { this.firstName = firstName; }

    public String getLastName()                           { return lastName; }
    public void setLastName(String lastName)              { this.lastName = lastName; }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (lastName != null)  sb.append(sb.length() > 0 ? " " : "").append(lastName);
        return sb.toString();
    }

    public String getEmail()                              { return email; }
    public void setEmail(String email)                    { this.email = email; }

    public String getRole()                               { return role; }
    public void setRole(String role)                      { this.role = role; }

    public boolean isActive()                             { return isActive; }
    public void setActive(boolean isActive)               { this.isActive = isActive; }

    public Timestamp getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)         { this.createdAt = createdAt; }

    public Timestamp getLastLogin()                       { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin)         { this.lastLogin = lastLogin; }
}
