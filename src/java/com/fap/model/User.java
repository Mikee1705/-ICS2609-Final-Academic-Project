package com.fap.model;

/**
 * Model: USERS table (Derby — LoginDB)
 *
 * Mirrors the schema used by Servlets.LoginServlet and Servlets.User:
 *   USERNAME   VARCHAR  (PK / unique)
 *   PASSWORD   VARCHAR  (AES-encrypted via Servlets.Security)
 *   USERROLE   VARCHAR  ('Admin' | 'Teacher' | 'Student' | 'Guest')
 *
 * Kept intentionally minimal so it stays compatible with the teammate's
 * existing Login flow. Extra display fields (full name, email, etc.) can
 * be added later if the schema is extended — until then, getFullName()
 * just returns the username so JSPs still render cleanly.
 *
 * IMPORTANT: the `password` field on this object holds the PLAINTEXT
 * value when read via UserDAO (which decrypts on the way out). It holds
 * the ENCRYPTED value when set by UserDAO before an INSERT/UPDATE.
 * Callers should never read `password` directly off the model unless
 * they know which side of the DAO boundary they're on.
 */
public class User {

    public enum Role { Admin, Teacher, Student, Guest }

    private String username;
    private String password;   // plaintext (after DAO decrypts) — never persist as-is
    private String role;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // ----------------------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------------------

    public String getUsername()              { return username; }
    public void   setUsername(String s)      { this.username = s; }

    public String getPassword()              { return password; }
    public void   setPassword(String s)      { this.password = s; }

    public String getRole()                  { return role; }
    public void   setRole(String s)          { this.role = s; }

    /**
     * Convenience for JSPs/PDFs that ask for a "full name".
     * The current Derby schema has no name columns, so we fall back to
     * the username. If the schema is extended later, override this.
     */
    public String getFullName() {
        return username == null ? "" : username;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', role='" + role + "'}";
    }
}
