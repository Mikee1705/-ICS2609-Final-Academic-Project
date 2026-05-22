package Servlets;

import com.fap.dao.mysql.CourseDAO;
import com.fap.model.Course;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * CourseServlet
 *
 * Single controller for the Courses page. Handles:
 *
 *   GET  /CourseServlet                 → list all courses, forward to JSP
 *   POST /CourseServlet?action=add      → insert
 *   POST /CourseServlet?action=edit     → update
 *   POST /CourseServlet?action=delete   → delete
 *
 * Reads form fields:
 *   CourseId        (edit/delete only)
 *   CourseCode
 *   CourseName
 *   CourseDesc
 *   Category
 *   Level
 *   DurationHours
 *   MaxStudents
 *
 * After every mutating action, redirects back to /CourseServlet so the
 * page reloads with the fresh list (PRG pattern — Post/Redirect/Get).
 */
@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            CourseDAO dao = new CourseDAO(getServletContext());
            List<Course> courses = dao.getAllCourses();
            req.setAttribute("courses", courses);
            req.getRequestDispatcher("/Courses_CRUD.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load courses", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            CourseDAO dao = new CourseDAO(getServletContext());

            switch (action.toLowerCase()) {
                case "add":    handleAdd(req, dao);    break;
                case "edit":   handleEdit(req, dao);   break;
                case "delete": handleDelete(req, dao); break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                    return;
            }

            // PRG — redirect after mutation so refresh doesn't re-submit
            resp.sendRedirect(req.getContextPath() + "/CourseServlet");

        } catch (SQLException e) {
            throw new ServletException("Course operation failed: " + action, e);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number field");
        }
    }

    // ----------------------------------------------------------------
    // ACTION HANDLERS
    // ----------------------------------------------------------------

    private void handleAdd(HttpServletRequest req, CourseDAO dao) throws SQLException {
        Course c = buildCourseFromRequest(req);
        dao.insertCourse(c);
    }

    private void handleEdit(HttpServletRequest req, CourseDAO dao) throws SQLException {
        Course c = buildCourseFromRequest(req);
        c.setCourseId(parseInt(req.getParameter("CourseId"), 0));
        dao.updateCourse(c);
    }

    private void handleDelete(HttpServletRequest req, CourseDAO dao) throws SQLException {
        int id = parseInt(req.getParameter("CourseId"), 0);
        if (id > 0) dao.deleteCourse(id);
    }

    /** Maps form fields → Course POJO with sane defaults for missing fields. */
    private Course buildCourseFromRequest(HttpServletRequest req) {
        Course c = new Course();
        c.setCourseCode(orDefault(req.getParameter("CourseCode"), "TBD"));
        c.setCourseName(orDefault(req.getParameter("CourseName"), ""));
        c.setDescription(orDefault(req.getParameter("CourseDesc"), ""));
        c.setCategory(orDefault(req.getParameter("Category"), "General"));
        c.setLevel(orDefault(req.getParameter("Level"), "Beginner"));
        c.setDurationHours(parseInt(req.getParameter("DurationHours"), 0));
        c.setMaxStudents(parseInt(req.getParameter("MaxStudents"), 30));
        c.setThumbnailUrl(req.getParameter("ThumbnailUrl"));
        c.setActive(true);
        return c;
    }

    private static String orDefault(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }

    private static int parseInt(String s, int def) {
        try { return s == null ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
}
