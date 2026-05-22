package com.fap.dao.derby;

import com.fap.db.DerbyConnection;
import com.fap.model.User;
import com.fap.util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * UserDAO (Derby)
 *
 * Manages the USERS table used for authentication and user-list reports.
 * Passwords are stored as salted SHA-256 ("saltHex:hashHex") via PasswordHasher.
 */
public class UserDAO {

    private final ServletContext context;

    public UserDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    /** Returns ALL users — used by the User List PDF report. */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY ROLE, LAST_NAME, FIRST_NAME";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        }
        return list;
    }

    /** Returns a user by username. Used for login. */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    /** Returns a user by ID. */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    /**
     * Verifies username/password using salted SHA-256.
     * Returns the User on success, null on failure.
     */
    public User authenticate(String username, String plainPassword) throws SQLException {
        User u = getUserByUsername(username);
        if (u == null || !u.isActive()) return null;
        if (PasswordHasher.verify(plainPassword, u.getPasswordHash())) {
            return u;
        }
        return null;
    }

    /** Inserts a new user. Password is automatically hashed. */
    public int insertUser(User user, String plainPassword) throws SQLException {
        String sql = "INSERT INTO USERS (USERNAME, PASSWORD_HASH, FIRST_NAME, LAST_NAME, "
                   + "EMAIL, ROLE, IS_ACTIVE) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordHasher.hash(plainPassword));
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Updates the LAST_LOGIN timestamp for a user. */
    public void touchLastLogin(int userId) throws SQLException {
        String sql = "UPDATE USERS SET LAST_LOGIN = CURRENT_TIMESTAMP WHERE USER_ID = ?";

        try (Connection conn = DerbyConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("USER_ID"));
        u.setUsername(rs.getString("USERNAME"));
        u.setPasswordHash(rs.getString("PASSWORD_HASH"));
        u.setFirstName(rs.getString("FIRST_NAME"));
        u.setLastName(rs.getString("LAST_NAME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setRole(rs.getString("ROLE"));
        u.setActive(rs.getBoolean("IS_ACTIVE"));
        u.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        u.setLastLogin(rs.getTimestamp("LAST_LOGIN"));
        return u;
    }
}
