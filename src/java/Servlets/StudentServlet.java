package Servlets;

import com.fap.dao.derby.UserDAO;
import com.fap.dao.postgres.SalutationDAO;
import com.fap.dao.postgres.StudentDAO;
import com.fap.model.Salutation;
import com.fap.model.Student;
import com.fap.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * StudentServlet
 *
 * Cross-DBMS controller for the admin's Students CRUD page.
 *
 *   List view: pulls full profile data from PostgreSQL (Students table)
 *              joined with Salutations. Username column is the bridge to Derby.
 *   Add:       inserts into Derby USERS (for auth) AND Postgres Students (for profile)
 *   Delete:    removes from both DBs (Username + Student_ID required)
 *
 * Endpoints:
 *   GET  /StudentServlet                  → forward to Students_CRUD.jsp
 *   POST /StudentServlet?action=add       → insert
 *   POST /StudentServlet?action=delete    → delete
 */
@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            StudentDAO    studentDao    = new StudentDAO(getServletContext());
            SalutationDAO salutationDao = new SalutationDAO(getServletContext());

            List<Student>    students    = studentDao.getAllStudents();
            List<Salutation> salutations = salutationDao.getAllSalutations();

            req.setAttribute("students",    students);
            req.setAttribute("salutations", salutations);
            req.getRequestDispatcher("/Students_CRUD.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Failed to load students from PostgreSQL", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            switch (action.toLowerCase()) {
                case "add":    handleAdd(req);    break;
                case "delete": handleDelete(req); break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                    return;
            }
            resp.sendRedirect(req.getContextPath() + "/StudentServlet");

        } catch (SQLException e) {
            throw new ServletException("Student operation failed: " + action, e);
        }
    }

    // ----------------------------------------------------------------

    private void handleAdd(HttpServletRequest req) throws SQLException {
        String studentId = trim(req.getParameter("StudentId"));
        String username  = trim(req.getParameter("Username"));
        String firstName = trim(req.getParameter("FirstName"));
        String lastName  = trim(req.getParameter("LastName"));
        String email     = trim(req.getParameter("Email"));
        String funding   = trim(req.getParameter("Funding"));
        String pw        = req.getParameter("Password");
        if (pw == null || pw.isEmpty()) pw = "Password123";

        Integer salutationId = null;
        String salRaw = req.getParameter("SalutationId");
        if (salRaw != null && !salRaw.isEmpty()) {
            try { salutationId = Integer.valueOf(salRaw); } catch (NumberFormatException ignored) {}
        }

        // 1. Derby — create the auth record
        UserDAO userDao = new UserDAO(getServletContext());
        User u = new User(username, null, "Student");
        userDao.insertUser(u, pw);

        // 2. Postgres — create the profile record
        StudentDAO studentDao = new StudentDAO(getServletContext());
        Student s = new Student();
        s.setStudentId(studentId);
        s.setUsername(username);
        s.setSalutationId(salutationId);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setFunding(funding);
        s.setRegistrationDate(new java.sql.Date(System.currentTimeMillis()));
        studentDao.insertStudent(s);
    }

    private void handleDelete(HttpServletRequest req) throws SQLException {
        String username  = trim(req.getParameter("Username"));
        String studentId = trim(req.getParameter("StudentId"));

        // Postgres first (cascades to phones), then Derby
        if (!studentId.isEmpty()) new StudentDAO(getServletContext()).deleteStudent(studentId);
        if (!username.isEmpty())  new UserDAO(getServletContext()).deleteUser(username);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
