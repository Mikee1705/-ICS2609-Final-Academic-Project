package com.fap.dao.mysql;

import com.fap.db.MySQLConnection;
import com.fap.model.Enrollment;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * EnrollmentDAO
 * Handles all CRUD and report queries for the Enrollments table.
 * Database: fap_activelearning
 * Enrollment_Date is the key field for time-bound reports.
 */
public class EnrollmentDAO {

    private final ServletContext context;

    public EnrollmentDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    /** Returns all enrollments with course name (via JOIN). */
    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Course_Name FROM Enrollments e "
                   + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                   + "ORDER BY e.Enrollment_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEnrollment(rs));
        }
        return list;
    }

    /** Returns all enrollments for a specific student. */
    public List<Enrollment> getEnrollmentsByStudent(int studentId) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Course_Name FROM Enrollments e "
                   + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                   + "WHERE e.Student_ID = ? ORDER BY e.Enrollment_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    /** Returns all enrollments for a specific course. */
    public List<Enrollment> getEnrollmentsByCourse(int courseId) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Course_Name FROM Enrollments e "
                   + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                   + "WHERE e.Course_ID = ? ORDER BY e.Enrollment_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    /** Inserts a new enrollment. Returns generated Enrollment_ID. */
    public int insertEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO Enrollments (Student_ID, Course_ID, Enrollment_Date, Status, "
                   + "Progress_Percent, Grade, Certificate_Issued) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setDate(3, enrollment.getEnrollmentDate() != null
                    ? enrollment.getEnrollmentDate()
                    : new Date(System.currentTimeMillis()));
            ps.setString(4, enrollment.getStatus() != null ? enrollment.getStatus() : "Active");
            ps.setBigDecimal(5, enrollment.getProgressPercent() != null
                    ? enrollment.getProgressPercent()
                    : BigDecimal.ZERO);
            ps.setBigDecimal(6, enrollment.getGrade());
            ps.setInt(7, enrollment.isCertificateIssued() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Updates the status of an enrollment. Fills Completion_Date when set to Completed. */
    public boolean updateStatus(int enrollmentId, String status) throws SQLException {
        String sql = "UPDATE Enrollments SET Status = ?, "
                   + "Completion_Date = CASE WHEN ? = 'Completed' THEN CURRENT_DATE ELSE NULL END "
                   + "WHERE Enrollment_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Updates the progress percentage for an enrollment. */
    public boolean updateProgress(int enrollmentId, BigDecimal progress) throws SQLException {
        String sql = "UPDATE Enrollments SET Progress_Percent = ?, Last_Accessed = CURRENT_TIMESTAMP "
                   + "WHERE Enrollment_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, progress);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Deletes an enrollment by ID. */
    public boolean deleteEnrollment(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM Enrollments WHERE Enrollment_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // REPORT QUERIES
    // ----------------------------------------------------------------

    /**
     * Time-bound report: ALL enrollments between two dates.
     * Used for admin PDF report — "print all records."
     */
    public List<Enrollment> getAllEnrollmentsByDateRange(Date fromDate, Date toDate) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Course_Name FROM Enrollments e "
                   + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                   + "WHERE e.Enrollment_Date BETWEEN ? AND ? "
                   + "ORDER BY e.Enrollment_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    /**
     * Time-bound report: enrollments for a SPECIFIC student between two dates.
     * Used for admin PDF report — "print only their records."
     */
    public List<Enrollment> getEnrollmentsByStudentAndDateRange(int studentId, Date fromDate, Date toDate)
            throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Course_Name FROM Enrollments e "
                   + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                   + "WHERE e.Student_ID = ? AND e.Enrollment_Date BETWEEN ? AND ? "
                   + "ORDER BY e.Enrollment_Date DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    /**
     * Completion rate analytics — counts by status.
     * Returns: [status, count] pairs for dashboard charts.
     */
    public List<Object[]> getEnrollmentStatusSummary() throws SQLException {
        List<Object[]> summary = new ArrayList<>();
        String sql = "SELECT Status, COUNT(*) AS Total FROM Enrollments GROUP BY Status";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                summary.add(new Object[]{ rs.getString("Status"), rs.getInt("Total") });
            }
        }
        return summary;
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("Enrollment_ID"));
        e.setStudentId(rs.getInt("Student_ID"));
        e.setCourseId(rs.getInt("Course_ID"));
        e.setEnrollmentDate(rs.getDate("Enrollment_Date"));
        e.setCompletionDate(rs.getDate("Completion_Date"));
        e.setStatus(rs.getString("Status"));
        e.setProgressPercent(rs.getBigDecimal("Progress_Percent"));
        e.setGrade(rs.getBigDecimal("Grade"));
        e.setCertificateIssued(rs.getInt("Certificate_Issued") == 1);
        e.setLastAccessed(rs.getTimestamp("Last_Accessed"));
        try { e.setCourseName(rs.getString("Course_Name")); } catch (SQLException ignored) {}
        return e;
    }
}
