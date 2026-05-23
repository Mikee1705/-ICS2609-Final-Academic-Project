package Servlets;

import com.fap.dao.derby.UserDAO;
import com.fap.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * InstructorServlet
 *
 * Lists/edits users where USERROLE = 'Teacher' (Derby).
 *
 * Endpoints:
 *   GET  /InstructorServlet               → forward to Instructors_CRUD.jsp
 *   POST /InstructorServlet?action=add    → insert new teacher
 *   POST /InstructorServlet?action=delete → delete by username
 */
@WebServlet("/InstructorServlet")
public class InstructorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            UserDAO dao = new UserDAO(getServletContext());
            List<User> teachers = dao.getUsersByRole("Teacher");
            req.setAttribute("teachers", teachers);
            req.getRequestDispatcher("/Instructors_CRUD.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load instructors", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            UserDAO dao = new UserDAO(getServletContext());

            switch (action.toLowerCase()) {
                case "add":    handleAdd(req, dao);    break;
                case "delete": handleDelete(req, dao); break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                    return;
            }

            resp.sendRedirect(req.getContextPath() + "/InstructorServlet");

        } catch (SQLException e) {
            throw new ServletException("Instructor operation failed: " + action, e);
        }
    }

    // ----------------------------------------------------------------

    private void handleAdd(HttpServletRequest req, UserDAO dao) throws SQLException {
        String username = trim(req.getParameter("Username"));
        String pw       = req.getParameter("Password");
        if (pw == null || pw.isEmpty()) pw = "Password123";

        User u = new User(username, null, "Teacher");
        dao.insertUser(u, pw);
    }

    private void handleDelete(HttpServletRequest req, UserDAO dao) throws SQLException {
        String username = trim(req.getParameter("Username"));
        if (!username.isEmpty()) dao.deleteUser(username);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
