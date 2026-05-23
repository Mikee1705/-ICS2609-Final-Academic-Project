package com.fap.dao.mysql;

import com.fap.db.MySQLConnection;
import com.fap.model.CourseRating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * CourseRatingDAO
 * Handles all CRUD and report queries for the Course_Ratings table.
 * Database: fap_activelearning
 * Rating_Score must always be between 1 and 5.
 */
public class CourseRatingDAO {

    private final ServletContext context;

    public CourseRatingDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    /** Returns all ratings with course name. */
    public List<CourseRating> getAllRatings() throws SQLException {
        List<CourseRating> list = new ArrayList<>();
        String sql = "SELECT cr.*, c.Course_Name FROM Course_Ratings cr "
                   + "JOIN Courses c ON cr.Course_ID = c.Course_ID "
                   + "ORDER BY cr.Rated_At DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRating(rs));
        }
        return list;
    }

    /** Returns all ratings submitted by a specific student. */
    public List<CourseRating> getRatingsByStudent(String studentId) throws SQLException {
        List<CourseRating> list = new ArrayList<>();
        String sql = "SELECT cr.*, c.Course_Name FROM Course_Ratings cr "
                   + "JOIN Courses c ON cr.Course_ID = c.Course_ID "
                   + "WHERE cr.Student_ID = ? ORDER BY cr.Rated_At DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRating(rs));
            }
        }
        return list;
    }

    /** Returns all ratings for a specific course. */
    public List<CourseRating> getRatingsByCourse(int courseId) throws SQLException {
        List<CourseRating> list = new ArrayList<>();
        String sql = "SELECT cr.*, c.Course_Name FROM Course_Ratings cr "
                   + "JOIN Courses c ON cr.Course_ID = c.Course_ID "
                   + "WHERE cr.Course_ID = ? ORDER BY cr.Rated_At DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRating(rs));
            }
        }
        return list;
    }

    /** Returns the average rating score for a course. Returns 0.0 if none exist. */
    public double getAverageRating(int courseId) throws SQLException {
        String sql = "SELECT AVG(Rating_Score) AS Avg_Rating FROM Course_Ratings WHERE Course_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("Avg_Rating");
            }
        }
        return 0.0;
    }

    /** Inserts a new rating. Returns generated Rating_ID. */
    public int insertRating(CourseRating rating) throws SQLException {
        if (rating.getRatingScore() < 1 || rating.getRatingScore() > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }

        String sql = "INSERT INTO Course_Ratings (Student_ID, Course_ID, Rating_Score, "
                   + "Review_Text, Would_Recommend, Is_Verified) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rating.getStudentId());
            ps.setInt(2, rating.getCourseId());
            ps.setInt(3, rating.getRatingScore());
            ps.setString(4, rating.getReviewText());
            if (rating.getWouldRecommend() != null) {
                ps.setInt(5, rating.getWouldRecommend() ? 1 : 0);
            } else {
                ps.setNull(5, Types.TINYINT);
            }
            ps.setInt(6, rating.isVerified() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Updates an existing rating score and review. */
    public boolean updateRating(int ratingId, int newScore, String reviewText) throws SQLException {
        if (newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }

        String sql = "UPDATE Course_Ratings SET Rating_Score = ?, Review_Text = ? WHERE Rating_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newScore);
            ps.setString(2, reviewText);
            ps.setInt(3, ratingId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Deletes a rating by ID. */
    public boolean deleteRating(int ratingId) throws SQLException {
        String sql = "DELETE FROM Course_Ratings WHERE Rating_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ratingId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // REPORT QUERIES
    // ----------------------------------------------------------------

    /**
     * Time-bound report: ratings submitted within a date range.
     * Used for admin PDF report.
     */
    public List<CourseRating> getRatingsByDateRange(Timestamp fromDate, Timestamp toDate)
            throws SQLException {
        List<CourseRating> list = new ArrayList<>();
        String sql = "SELECT cr.*, c.Course_Name FROM Course_Ratings cr "
                   + "JOIN Courses c ON cr.Course_ID = c.Course_ID "
                   + "WHERE cr.Rated_At BETWEEN ? AND ? "
                   + "ORDER BY cr.Rated_At DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, fromDate);
            ps.setTimestamp(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRating(rs));
            }
        }
        return list;
    }

    /**
     * Analytics: average rating per course.
     * Returns: [courseName, averageScore, totalRatings] — useful for dashboard.
     */
    public List<Object[]> getAverageRatingsPerCourse() throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT c.Course_Name, AVG(cr.Rating_Score) AS Avg_Score, "
                   + "COUNT(*) AS Total_Ratings "
                   + "FROM Course_Ratings cr "
                   + "JOIN Courses c ON cr.Course_ID = c.Course_ID "
                   + "GROUP BY c.Course_ID, c.Course_Name "
                   + "ORDER BY Avg_Score DESC";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("Course_Name"),
                    rs.getDouble("Avg_Score"),
                    rs.getInt("Total_Ratings")
                });
            }
        }
        return result;
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------

    private CourseRating mapRating(ResultSet rs) throws SQLException {
        CourseRating r = new CourseRating();
        r.setRatingId(rs.getInt("Rating_ID"));
        r.setStudentId(rs.getString("Student_ID"));
        r.setCourseId(rs.getInt("Course_ID"));
        r.setRatingScore(rs.getInt("Rating_Score"));
        r.setReviewText(rs.getString("Review_Text"));
        int rec = rs.getInt("Would_Recommend");
        r.setWouldRecommend(rs.wasNull() ? null : (rec == 1));
        r.setVerified(rs.getInt("Is_Verified") == 1);
        r.setRatedAt(rs.getTimestamp("Rated_At"));
        try { r.setCourseName(rs.getString("Course_Name")); } catch (SQLException ignored) {}
        return r;
    }
}
