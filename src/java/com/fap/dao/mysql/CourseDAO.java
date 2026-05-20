package com.fap.dao.mysql;

import com.fap.db.MySQLConnection;
import com.fap.model.Course;
import com.fap.model.CourseAssignment;
import com.fap.model.CourseLesson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * CourseDAO
 * Handles all CRUD and report queries for Courses, Course_Assignments, Course_Lessons.
 * Database: fap_activelearning
 */
public class CourseDAO {

    private final ServletContext context;

    public CourseDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // COURSES — CRUD
    // ----------------------------------------------------------------

    /** Returns all active courses. */
    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM Courses ORDER BY Course_Name";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCourse(rs));
        }
        return list;
    }

    /** Returns a single course by ID. */
    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT * FROM Courses WHERE Course_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCourse(rs);
            }
        }
        return null;
    }

    /** Inserts a new course. Returns the generated Course_ID. */
    public int insertCourse(Course course) throws SQLException {
        String sql = "INSERT INTO Courses (Course_Code, Course_Name, Description, Category, Level, "
                   + "Duration_Hours, Max_Students, Thumbnail_URL, Is_Active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getCategory());
            ps.setString(5, course.getLevel());
            ps.setInt(6, course.getDurationHours());
            ps.setInt(7, course.getMaxStudents());
            ps.setString(8, course.getThumbnailUrl());
            ps.setInt(9, course.isActive() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Updates an existing course. */
    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE Courses SET Course_Code = ?, Course_Name = ?, Description = ?, "
                   + "Category = ?, Level = ?, Duration_Hours = ?, Max_Students = ?, "
                   + "Thumbnail_URL = ?, Is_Active = ? WHERE Course_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getCategory());
            ps.setString(5, course.getLevel());
            ps.setInt(6, course.getDurationHours());
            ps.setInt(7, course.getMaxStudents());
            ps.setString(8, course.getThumbnailUrl());
            ps.setInt(9, course.isActive() ? 1 : 0);
            ps.setInt(10, course.getCourseId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Soft-deletes a course by setting Is_Active = 0. */
    public boolean deactivateCourse(int courseId) throws SQLException {
        String sql = "UPDATE Courses SET Is_Active = 0 WHERE Course_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Hard-deletes a course (cascades to lessons, assignments, enrollments, ratings). */
    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM Courses WHERE Course_ID = ?";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // COURSE ASSIGNMENTS — CRUD
    // ----------------------------------------------------------------

    /** Returns all assignments for a specific course. */
    public List<CourseAssignment> getAssignmentsByCourse(int courseId) throws SQLException {
        List<CourseAssignment> list = new ArrayList<>();
        String sql = "SELECT ca.*, c.Course_Name FROM Course_Assignments ca "
                   + "JOIN Courses c ON ca.Course_ID = c.Course_ID "
                   + "WHERE ca.Course_ID = ? ORDER BY ca.Due_Date";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapAssignment(rs));
            }
        }
        return list;
    }

    /** Inserts a new assignment. Returns the generated Assignment_ID. */
    public int insertAssignment(CourseAssignment a) throws SQLException {
        String sql = "INSERT INTO Course_Assignments (Course_ID, Title, Instructions, Assignment_Type, "
                   + "Due_Date, Max_Score, Passing_Score, Is_Required) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getInstructions());
            ps.setString(4, a.getAssignmentType());
            ps.setDate(5, a.getDueDate());
            ps.setBigDecimal(6, a.getMaxScore());
            ps.setBigDecimal(7, a.getPassingScore());
            ps.setInt(8, a.isRequired() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // COURSE LESSONS — CRUD
    // ----------------------------------------------------------------

    /** Returns all lessons for a specific course ordered by sequence. */
    public List<CourseLesson> getLessonsByCourse(int courseId) throws SQLException {
        List<CourseLesson> list = new ArrayList<>();
        String sql = "SELECT cl.*, c.Course_Name FROM Course_Lessons cl "
                   + "JOIN Courses c ON cl.Course_ID = c.Course_ID "
                   + "WHERE cl.Course_ID = ? ORDER BY cl.Lesson_Order";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLesson(rs));
            }
        }
        return list;
    }

    /** Inserts a new lesson. Returns the generated Lesson_ID. */
    public int insertLesson(CourseLesson lesson) throws SQLException {
        String sql = "INSERT INTO Course_Lessons (Course_ID, Lesson_Title, Content, Lesson_Type, "
                   + "Duration_Minutes, Lesson_Order, Resource_URL, Is_Preview) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lesson.getCourseId());
            ps.setString(2, lesson.getLessonTitle());
            ps.setString(3, lesson.getContent());
            ps.setString(4, lesson.getLessonType());
            ps.setInt(5, lesson.getDurationMinutes());
            ps.setInt(6, lesson.getLessonOrder());
            ps.setString(7, lesson.getResourceUrl());
            ps.setInt(8, lesson.isPreview() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // REPORT QUERIES
    // ----------------------------------------------------------------

    /**
     * Time-bound report: all courses with enrollment counts within a date range.
     * Used for admin PDF reports (From Date 1 to Date 2).
     */
    public List<Course> getCourseReportByDateRange(Date fromDate, Date toDate) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c "
                   + "LEFT JOIN Enrollments e ON c.Course_ID = e.Course_ID "
                   + "AND e.Enrollment_Date BETWEEN ? AND ? "
                   + "GROUP BY c.Course_ID ORDER BY c.Course_Name";

        try (Connection conn = MySQLConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCourse(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // MAPPERS
    // ----------------------------------------------------------------

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("Course_ID"));
        c.setCourseCode(rs.getString("Course_Code"));
        c.setCourseName(rs.getString("Course_Name"));
        c.setDescription(rs.getString("Description"));
        c.setCategory(rs.getString("Category"));
        c.setLevel(rs.getString("Level"));
        c.setDurationHours(rs.getInt("Duration_Hours"));
        c.setMaxStudents(rs.getInt("Max_Students"));
        c.setThumbnailUrl(rs.getString("Thumbnail_URL"));
        c.setActive(rs.getInt("Is_Active") == 1);
        try { c.setCreatedAt(rs.getTimestamp("Created_At")); } catch (SQLException ignored) {}
        try { c.setUpdatedAt(rs.getTimestamp("Updated_At")); } catch (SQLException ignored) {}
        return c;
    }

    private CourseAssignment mapAssignment(ResultSet rs) throws SQLException {
        CourseAssignment a = new CourseAssignment();
        a.setAssignmentId(rs.getInt("Assignment_ID"));
        a.setCourseId(rs.getInt("Course_ID"));
        a.setTitle(rs.getString("Title"));
        a.setInstructions(rs.getString("Instructions"));
        a.setAssignmentType(rs.getString("Assignment_Type"));
        a.setDueDate(rs.getDate("Due_Date"));
        a.setMaxScore(rs.getBigDecimal("Max_Score"));
        a.setPassingScore(rs.getBigDecimal("Passing_Score"));
        a.setRequired(rs.getInt("Is_Required") == 1);
        a.setCreatedAt(rs.getTimestamp("Created_At"));
        try { a.setCourseName(rs.getString("Course_Name")); } catch (SQLException ignored) {}
        return a;
    }

    private CourseLesson mapLesson(ResultSet rs) throws SQLException {
        CourseLesson l = new CourseLesson();
        l.setLessonId(rs.getInt("Lesson_ID"));
        l.setCourseId(rs.getInt("Course_ID"));
        l.setLessonTitle(rs.getString("Lesson_Title"));
        l.setContent(rs.getString("Content"));
        l.setLessonType(rs.getString("Lesson_Type"));
        l.setDurationMinutes(rs.getInt("Duration_Minutes"));
        l.setLessonOrder(rs.getInt("Lesson_Order"));
        l.setResourceUrl(rs.getString("Resource_URL"));
        l.setPreview(rs.getInt("Is_Preview") == 1);
        l.setCreatedAt(rs.getTimestamp("Created_At"));
        try { l.setCourseName(rs.getString("Course_Name")); } catch (SQLException ignored) {}
        return l;
    }
}
