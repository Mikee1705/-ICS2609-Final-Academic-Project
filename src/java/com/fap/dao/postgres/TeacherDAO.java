package com.fap.dao.postgres;

import com.fap.db.PostgresConnection;
import com.fap.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * TeacherDAO — PostgreSQL (public.Teachers + Salutations)
 *
 * The Postgres "Username" column is the identity bridge between this DB
 * and Derby. See ARCHITECTURE.md for the cross-DBMS data flow.
 */
public class TeacherDAO {

    private final ServletContext context;

    public TeacherDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    public List<Teacher> getAllTeachers() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT t.*, sal.Title AS Salutation_Title "
                   + "FROM Teachers t "
                   + "LEFT JOIN Salutations sal ON t.Salutation_ID = sal.Salutation_ID "
                   + "ORDER BY t.Last_Name, t.First_Name";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapTeacher(rs));
        }
        return list;
    }

    public Teacher getTeacherById(String teacherId) throws SQLException {
        String sql = "SELECT t.*, sal.Title AS Salutation_Title "
                   + "FROM Teachers t "
                   + "LEFT JOIN Salutations sal ON t.Salutation_ID = sal.Salutation_ID "
                   + "WHERE t.Teacher_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTeacher(rs);
            }
        }
        return null;
    }

    /**
     * Identity bridge — find a Postgres teacher profile by their Derby USERNAME.
     */
    public Teacher getTeacherByUsername(String username) throws SQLException {
        String sql = "SELECT t.*, sal.Title AS Salutation_Title "
                   + "FROM Teachers t "
                   + "LEFT JOIN Salutations sal ON t.Salutation_ID = sal.Salutation_ID "
                   + "WHERE t.Username = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTeacher(rs);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // WRITE
    // ----------------------------------------------------------------

    public boolean insertTeacher(Teacher t) throws SQLException {
        String sql = "INSERT INTO Teachers "
                   + "(Teacher_ID, Salutation_ID, First_Name, Last_Name, Username) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTeacherId());
            if (t.getSalutationId() != null) ps.setInt(2, t.getSalutationId());
            else                              ps.setNull(2, Types.INTEGER);
            ps.setString(3, t.getFirstName());
            ps.setString(4, t.getLastName());
            ps.setString(5, t.getUsername());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateTeacher(Teacher t) throws SQLException {
        String sql = "UPDATE Teachers SET "
                   + "Salutation_ID = ?, First_Name = ?, Last_Name = ? "
                   + "WHERE Teacher_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (t.getSalutationId() != null) ps.setInt(1, t.getSalutationId());
            else                              ps.setNull(1, Types.INTEGER);
            ps.setString(2, t.getFirstName());
            ps.setString(3, t.getLastName());
            ps.setString(4, t.getTeacherId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTeacher(String teacherId) throws SQLException {
        String sql = "DELETE FROM Teachers WHERE Teacher_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getString("Teacher_ID"));
        int sid = rs.getInt("Salutation_ID");
        t.setSalutationId(rs.wasNull() ? null : sid);
        t.setFirstName(rs.getString("First_Name"));
        t.setLastName(rs.getString("Last_Name"));
        t.setUsername(rs.getString("Username"));
        try { t.setSalutationTitle(rs.getString("Salutation_Title")); }
        catch (SQLException ignored) { /* column not present */ }
        return t;
    }
}
