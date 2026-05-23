package Servlets;

import com.fap.dao.mysql.CourseDAO;
import com.fap.dao.mysql.CourseRatingDAO;
import com.fap.dao.mysql.EnrollmentDAO;
import com.fap.model.Course;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * AnalyticsServlet
 *
 * Populates AdminAnalytics.jsp with real DB data:
 *
 *   • courseNames        — for X-axis on all charts
 *   • enrolledCounts     — enrollment volume per course
 *   • avgCourseRatings   — average rating per course (0–5)
 *   • completionRates    — % of enrollments marked Completed per course
 *   • statusSummary      — overall Active/Completed/Dropped counts
 *
 * Endpoint:  GET /Analytics
 */
@WebServlet("/Analytics")
public class AnalyticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            CourseDAO       courseDao    = new CourseDAO(getServletContext());
            EnrollmentDAO   enrollDao    = new EnrollmentDAO(getServletContext());
            CourseRatingDAO ratingDao    = new CourseRatingDAO(getServletContext());

            List<Course> courses = courseDao.getAllCourses();

            // Per-course aggregates
            List<String>  names           = new ArrayList<>();
            List<Integer> enrolledCounts  = new ArrayList<>();
            List<Double>  avgRatings      = new ArrayList<>();
            List<Double>  completionRates = new ArrayList<>();

            // Build a fast lookup of avg-ratings per course
            Map<String, Double> ratingsByCourse = new HashMap<>();
            for (Object[] row : ratingDao.getAverageRatingsPerCourse()) {
                ratingsByCourse.put((String) row[0], (Double) row[1]);
            }

            for (Course c : courses) {
                names.add(c.getCourseName());

                int total     = enrollDao.getEnrollmentsByCourse(c.getCourseId()).size();
                long completed = enrollDao.getEnrollmentsByCourse(c.getCourseId()).stream()
                        .filter(e -> "Completed".equalsIgnoreCase(e.getStatus()))
                        .count();

                enrolledCounts.add(total);
                avgRatings.add(ratingsByCourse.getOrDefault(c.getCourseName(), 0.0));
                completionRates.add(total == 0 ? 0.0 : (completed * 100.0) / total);
            }

            req.setAttribute("courseNames",      names);
            req.setAttribute("enrolledCounts",   enrolledCounts);
            req.setAttribute("avgCourseRatings", avgRatings);
            req.setAttribute("completionRates",  completionRates);
            req.setAttribute("statusSummary",    enrollDao.getEnrollmentStatusSummary());

            req.getRequestDispatcher("/AdminAnalytics.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Failed to load analytics", e);
        }
    }
}
