package Servlets;

import com.fap.dao.derby.UserDAO;
import com.fap.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * InstructorServlet
 *
 * Lists/edits users where ROLE = 'Teacher'. Source: Derby USERS table.
 *
 * Endpoints:
 *   GET  /InstructorServlet               → forward to Instructors_CRUD.jsp
 *   POST /InstructorServlet?action=add    → insert new teacher
 *   POST /InstructorServlet?action=edit   → update teacher (TBD)
 *   POST /InstructorServlet?action=delete → delete teacher (TBD)
 */
@WebServlet("/InstructorServlet")
public class InstructorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            UserDAO dao = new UserDAO(getServletContext());
            List<User> all = dao.getAllUsers();
            List<User> teachers = new ArrayList<>();
            for (User u : all) {
                if ("Teacher".equalsIgnoreCase(u.getRole())) teachers.add(u);
            }
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
                case "edit":   /* TODO */ break;
                case "delete": /* TODO */ break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                    return;
            }

            resp.sendRedirect(req.getContextPath() + "/InstructorServlet");

        } catch (SQLException e) {
            throw new ServletException("Instructor operation failed: " + action, e);
        }
    }

    private void handleAdd(HttpServletRequest req, UserDAO dao) throws SQLException {
        User u = new User();
        u.setUsername(orDefault(req.getParameter("Username"), ""));
        u.setFirstName(orDefault(req.getParameter("FirstName"), ""));
        u.setLastName(orDefault(req.getParameter("LastName"), ""));
        u.setEmail(orDefault(req.getParameter("Email"), ""));
        u.setRole("Teacher");
        u.setActive(true);

        String pw = req.getParameter("Password");
        if (pw == null || pw.isEmpty()) pw = "Password123";

        dao.insertUser(u, pw);
    }

    private static String orDefault(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }
}
