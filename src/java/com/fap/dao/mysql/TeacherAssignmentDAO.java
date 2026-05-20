package com.fap.dao.mysql;

import com.fap.db.MySQLConnection;
import com.fap.model.TeacherAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * TeacherAssignmentDAO
 * Handles all CRUD and report queries for the Teacher_Assignments table.
 * Database: fap_activelearning
 */
public class TeacherAssignmentDAO {

    private final ServletContext context;

    public TeacherAssignmentDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    /** Returns all teacher assignments with course name. */
    public List<TeacherAssignment> getAllTeacherAssignments() throws SQLException {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, c.Course_Name FROM Teacher_Assignments ta "
                   + "JOIN Courses c ON ta.Course_ID = c.Course_ID "
                   + "ORDER BY ta.Assigned_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapTeacherAssignment(rs));
        }
        return list;
    }

    /** Returns all courses assigned to a specific teacher. */
    public List<TeacherAssignment> getAssignmentsByTeacher(int teacherId) throws SQLException {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, c.Course_Name FROM Teacher_Assignments ta "
                   + "JOIN Courses c ON ta.Course_ID = c.Course_ID "
                   + "WHERE ta.Teacher_ID = ? ORDER BY ta.Assigned_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTeacherAssignment(rs));
            }
        }
        return list;
    }

    /** Assigns a teacher to a course. Returns generated Assignment_ID. */
    public int insertTeacherAssignment(TeacherAssignment ta) throws SQLException {
        String sql = "INSERT INTO Teacher_Assignments (Teacher_ID, Course_ID, Role, Assigned_Date, "
                   + "End_Date, Is_Active) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ta.getTeacherId());
            ps.setInt(2, ta.getCourseId());
            ps.setString(3, ta.getRole() != null ? ta.getRole() : "Primary");
            ps.setDate(4, ta.getAssignedDate() != null
                    ? ta.getAssignedDate()
                    : new Date(System.currentTimeMillis()));
            ps.setDate(5, ta.getEndDate());
            ps.setInt(6, ta.isActive() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Deactivates a teacher assignment (Is_Active = 0). */
    public boolean deactivateAssignment(int assignmentId) throws SQLException {
        String sql = "UPDATE Teacher_Assignments SET Is_Active = 0, End_Date = CURRENT_DATE "
                   + "WHERE Assignment_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Removes a teacher assignment by ID. */
    public boolean deleteTeacherAssignment(int assignmentId) throws SQLException {
        String sql = "DELETE FROM Teacher_Assignments WHERE Assignment_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // REPORT QUERIES
    // ----------------------------------------------------------------

    /**
     * Time-bound report: teacher assignments within a date range.
     */
    public List<TeacherAssignment> getAssignmentsByDateRange(Date fromDate, Date toDate)
            throws SQLException {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, c.Course_Name FROM Teacher_Assignments ta "
                   + "JOIN Courses c ON ta.Course_ID = c.Course_ID "
                   + "WHERE ta.Assigned_Date BETWEEN ? AND ? "
                   + "ORDER BY ta.Assigned_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTeacherAssignment(rs));
            }
        }
        return list;
    }

    /**
     * Time-bound report for a specific teacher within a date range.
     * Used for "print only their records."
     */
    public List<TeacherAssignment> getAssignmentsByTeacherAndDateRange(
            int teacherId, Date fromDate, Date toDate) throws SQLException {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, c.Course_Name FROM Teacher_Assignments ta "
                   + "JOIN Courses c ON ta.Course_ID = c.Course_ID "
                   + "WHERE ta.Teacher_ID = ? AND ta.Assigned_Date BETWEEN ? AND ? "
                   + "ORDER BY ta.Assigned_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTeacherAssignment(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------

    private TeacherAssignment mapTeacherAssignment(ResultSet rs) throws SQLException {
        TeacherAssignment ta = new TeacherAssignment();
        ta.setAssignmentId(rs.getInt("Assignment_ID"));
        ta.setTeacherId(rs.getInt("Teacher_ID"));
        ta.setCourseId(rs.getInt("Course_ID"));
        ta.setRole(rs.getString("Role"));
        ta.setAssignedDate(rs.getDate("Assigned_Date"));
        ta.setEndDate(rs.getDate("End_Date"));
        ta.setActive(rs.getInt("Is_Active") == 1);
        try { ta.setCourseName(rs.getString("Course_Name")); } catch (SQLException ignored) {}
        return ta;
    }
}
