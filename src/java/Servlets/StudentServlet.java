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
 * StudentServlet
 *
 * Lists/edits users where ROLE = 'Student'. Source: Derby USERS table.
 *
 * Endpoints:
 *   GET  /StudentServlet               → forward to Students_CRUD.jsp with student list
 *   POST /StudentServlet?action=add    → insert new student (password defaults to "Password123")
 *   POST /StudentServlet?action=edit   → update student
 *   POST /StudentServlet?action=delete → delete student
 *
 * Form fields:
 *   StudentId, Username, FirstName, LastName, Email, Password (only on add)
 */
@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            UserDAO dao = new UserDAO(getServletContext());
            List<User> all = dao.getAllUsers();
            List<User> students = new ArrayList<>();
            for (User u : all) {
                if ("Student".equalsIgnoreCase(u.getRole())) students.add(u);
            }
            req.setAttribute("students", students);
            req.getRequestDispatcher("/Students_CRUD.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load students", e);
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
                case "edit":   /* TODO: UserDAO.updateUser — add in next iteration */ break;
                case "delete": /* TODO: UserDAO.deleteUser — add in next iteration */ break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                    return;
            }

            resp.sendRedirect(req.getContextPath() + "/StudentServlet");

        } catch (SQLException e) {
            throw new ServletException("Student operation failed: " + action, e);
        }
    }

    private void handleAdd(HttpServletRequest req, UserDAO dao) throws SQLException {
        User u = new User();
        u.setUsername(orDefault(req.getParameter("Username"), ""));
        u.setFirstName(orDefault(req.getParameter("FirstName"), ""));
        u.setLastName(orDefault(req.getParameter("LastName"), ""));
        u.setEmail(orDefault(req.getParameter("Email"), ""));
        u.setRole("Student");
        u.setActive(true);

        String pw = req.getParameter("Password");
        if (pw == null || pw.isEmpty()) pw = "Password123"; // default — admin should reset later

        dao.insertUser(u, pw);
    }

    private static String orDefault(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }
}
