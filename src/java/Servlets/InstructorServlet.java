package Servlets;

import com.fap.dao.derby.UserDAO;
import com.fap.dao.postgres.SalutationDAO;
import com.fap.dao.postgres.TeacherDAO;
import com.fap.model.Salutation;
import com.fap.model.Teacher;
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
 * Cross-DBMS controller for the admin's Instructors CRUD page.
 *   List view: pulls profile data from PostgreSQL (Teachers + Salutations)
 *   Add:       inserts into Derby USERS (auth) AND Postgres Teachers (profile)
 *   Delete:    removes from both
 *
 * Endpoints:
 *   GET  /InstructorServlet                  → forward to Instructors_CRUD.jsp
 *   POST /InstructorServlet?action=add       → insert
 *   POST /InstructorServlet?action=delete    → delete
 */
@WebServlet("/InstructorServlet")
public class InstructorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthFilter.requireAdmin(req, resp)) return;

        try {
            TeacherDAO    teacherDao    = new TeacherDAO(getServletContext());
            SalutationDAO salutationDao = new SalutationDAO(getServletContext());

            List<Teacher>    teachers    = teacherDao.getAllTeachers();
            List<Salutation> salutations = salutationDao.getAllSalutations();

            req.setAttribute("teachers",    teachers);
            req.setAttribute("salutations", salutations);
            req.getRequestDispatcher("/Instructors_CRUD.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Failed to load instructors from PostgreSQL", e);
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
            resp.sendRedirect(req.getContextPath() + "/InstructorServlet");

        } catch (SQLException e) {
            throw new ServletException("Instructor operation failed: " + action, e);
        }
    }

    // ----------------------------------------------------------------

    private void handleAdd(HttpServletRequest req) throws SQLException {
        String teacherId = trim(req.getParameter("TeacherId"));
        String username  = trim(req.getParameter("Username"));
        String firstName = trim(req.getParameter("FirstName"));
        String lastName  = trim(req.getParameter("LastName"));
        String pw        = req.getParameter("Password");
        if (pw == null || pw.isEmpty()) pw = "Password123";

        Integer salutationId = null;
        String salRaw = req.getParameter("SalutationId");
        if (salRaw != null && !salRaw.isEmpty()) {
            try { salutationId = Integer.valueOf(salRaw); } catch (NumberFormatException ignored) {}
        }

        // Derby — auth
        UserDAO userDao = new UserDAO(getServletContext());
        User u = new User(username, null, "Teacher");
        userDao.insertUser(u, pw);

        // Postgres — profile
        TeacherDAO teacherDao = new TeacherDAO(getServletContext());
        Teacher t = new Teacher();
        t.setTeacherId(teacherId);
        t.setUsername(username);
        t.setSalutationId(salutationId);
        t.setFirstName(firstName);
        t.setLastName(lastName);
        teacherDao.insertTeacher(t);
    }

    private void handleDelete(HttpServletRequest req) throws SQLException {
        String username  = trim(req.getParameter("Username"));
        String teacherId = trim(req.getParameter("TeacherId"));

        if (!teacherId.isEmpty()) new TeacherDAO(getServletContext()).deleteTeacher(teacherId);
        if (!username.isEmpty())  new UserDAO(getServletContext()).deleteUser(username);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
