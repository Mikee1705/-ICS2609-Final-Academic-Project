package com.fap.dao.derby;

import Servlets.Security;
import com.fap.db.DerbyConnection;
import com.fap.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * UserDAO — Derby (LoginDB.USERS)
 *
 * RECONCILED VERSION
 * ------------------
 * Aligned with teammate 2's existing schema and encryption choice so the
 * login flow and admin CRUD now agree.
 *
 * Schema (must match what LoginServlet expects):
 *   USERNAME   VARCHAR(50)  PK
 *   PASSWORD   VARCHAR(255) — AES/ECB/PKCS5Padding via Servlets.Security
 *   USERROLE   VARCHAR(20)  — 'Admin' / 'Teacher' / 'Student' / 'Guest'
 *
 * Encryption: We delegate to Servlets.Security so there is exactly ONE
 * crypto implementation in the codebase. The EncryptionKey param in
 * web.xml is the single source of truth.
 *
 * Modularity note: this class lives at com.fap.dao.derby.* on purpose.
 * When the PostgreSQL teammate's table arrives, drop a sibling class
 * at com.fap.dao.postgres.* — no changes needed here.
 */
public class UserDAO {

    private final ServletContext context;

    public UserDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /** Returns all users, ordered by role then username. */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT USERNAME, PASSWORD, USERROLE FROM USERS "
                   + "ORDER BY USERROLE, USERNAME";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        }
        return list;
    }

    /**
     * Returns all users with a specific role (e.g. "Student", "Teacher").
     * Case-insensitive — matches the login flow which uses equalsIgnoreCase.
     */
    public List<User> getUsersByRole(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT USERNAME, PASSWORD, USERROLE FROM USERS "
                   + "WHERE UPPER(USERROLE) = UPPER(?) ORDER BY USERNAME";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapUser(rs));
            }
        }
        return list;
    }

    /** Returns a user by USERNAME, or null if not found. */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT USERNAME, PASSWORD, USERROLE FROM USERS WHERE USERNAME = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    /**
     * Verify credentials (used by LoginServlet — optional alternative path).
     * Returns the User on success, null on failure.
     */
    public User authenticate(String username, String plainPassword) throws SQLException {
        User u = getUserByUsername(username);
        if (u == null) return null;
        if (plainPassword != null && plainPassword.equals(u.getPassword())) {
            return u;
        }
        return null;
    }

    // ----------------------------------------------------------------
    // WRITE
    // ----------------------------------------------------------------

    /**
     * Inserts a new user. The plaintext password is AES-encrypted
     * via Servlets.Security before being stored.
     */
    public boolean insertUser(User user, String plainPassword) throws SQLException {
        String sql = "INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES (?, ?, ?)";

        String encrypted = Security.Encrypt(context, plainPassword);
        if (encrypted == null) {
            throw new SQLException("Password encryption failed — check EncryptionKey in web.xml.");
        }

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, encrypted);
            ps.setString(3, user.getRole());
            return ps.executeUpdate() > 0;
        }
    }

    /** Update a user's role. */
    public boolean updateRole(String username, String newRole) throws SQLException {
        String sql = "UPDATE USERS SET USERROLE = ? WHERE USERNAME = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

    /** Update a user's password (will be AES-encrypted). */
    public boolean updatePassword(String username, String newPlainPassword) throws SQLException {
        String sql = "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?";

        String encrypted = Security.Encrypt(context, newPlainPassword);
        if (encrypted == null) {
            throw new SQLException("Password encryption failed.");
        }

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, encrypted);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

    /** Delete a user by USERNAME. */
    public boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM USERS WHERE USERNAME = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // INTERNAL — ResultSet → User
    // ----------------------------------------------------------------

    /**
     * Maps a row of (USERNAME, PASSWORD, USERROLE) to a User object.
     * PASSWORD is decrypted here so callers always see plaintext.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        String encryptedPw = rs.getString("PASSWORD");
        String plainPw     = (encryptedPw != null) ? Security.Decrypt(context, encryptedPw.trim()) : null;

        User u = new User();
        u.setUsername(rs.getString("USERNAME") == null ? "" : rs.getString("USERNAME").trim());
        u.setPassword(plainPw);
        u.setRole(rs.getString("USERROLE") == null ? "" : rs.getString("USERROLE").trim());
        return u;
    }
}
