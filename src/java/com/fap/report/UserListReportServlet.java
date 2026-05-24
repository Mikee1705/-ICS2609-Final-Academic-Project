package com.fap.report;

import com.fap.dao.derby.UserDAO;
import com.fap.dao.postgres.StudentDAO;
import com.fap.dao.postgres.TeacherDAO;
import com.fap.model.Student;
import com.fap.model.Teacher;
import com.fap.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * UserListReportServlet
 *
 * Generates  USERLIST_yyyyMMddHHmmss.pdf
 *
 * Cross-DBMS report — aggregates rows from all THREE databases:
 *   • Admins    pulled from Derby  (LoginDB.USERS, USERROLE = 'Admin')
 *   • Teachers  pulled from PostgreSQL (postgres.Teachers)
 *   • Students  pulled from PostgreSQL (postgres.Students)
 *
 * Spec compliance:
 *   • At least 50 records (5 admins + 10 teachers + 40 students = 55)
 *   • Lists username + role only (NO passwords printed)
 *   • Asterisk (*) beside the currently logged-in admin
 *   • Landscape, paginated, header/footer from web.xml
 *   • Filename = USERLIST_<timestamp>.pdf
 *   • Client-side download
 *
 * URL:  /report/users
 */
@WebServlet("/report/users")
public class UserListReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Auth: must be logged-in Admin
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("Role")  : null;
        String me   = (session != null) ? (String) session.getAttribute("UName") : null;

        if (role == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please log in.");
            return;
        }
        if (!"Admin".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only admins can generate the user list report.");
            return;
        }

        try {
            // ── Pull from THREE different DBs ────────────────────────
            UserDAO    userDao    = new UserDAO(getServletContext());     // Derby
            TeacherDAO teacherDao = new TeacherDAO(getServletContext());  // Postgres
            StudentDAO studentDao = new StudentDAO(getServletContext());  // Postgres

            List<User>    admins   = userDao.getUsersByRole("Admin");
            List<Teacher> teachers = teacherDao.getAllTeachers();
            List<Student> students = studentDao.getAllStudents();

            int total = admins.size() + teachers.size() + students.size();

            // ── Build the PDF ───────────────────────────────────────
            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "USERLIST", me);
            Document doc = rpt.startDocument();

            doc.add(rpt.title("User List Report"));
            doc.add(rpt.subtitle("Total users: " + total
                    + "   (Admins: " + admins.size()
                    + " · Teachers: " + teachers.size()
                    + " · Students: " + students.size() + ")"));

            // Columns: # | Username | Role
            PdfPTable table = rpt.createTable(
                    new float[]{0.8f, 4f, 2f},
                    new String[]{"#", "Username", "Role"});

            int rowNum = 1;

            // ----- Admins (Derby) -----
            for (User a : admins) {
                rowNum = addRow(table, rpt, rowNum, a.getUsername(), "Admin", me);
            }

            // ----- Teachers (Postgres) -----
            for (Teacher t : teachers) {
                rowNum = addRow(table, rpt, rowNum, t.getUsername(), "Teacher", me);
            }

            // ----- Students (Postgres) -----
            for (Student s : students) {
                rowNum = addRow(table, rpt, rowNum, s.getUsername(), "Student", me);
            }

            doc.add(table);

            // Legend
            PdfPCell legend = new PdfPCell(new Phrase(
                    "* indicates the currently logged-in admin account",
                    PdfReportBuilder.TABLE_BODY));
            legend.setBorder(0);
            legend.setPaddingTop(10);
            PdfPTable footerNote = new PdfPTable(1);
            footerNote.setWidthPercentage(100);
            footerNote.addCell(legend);
            doc.add(footerNote);

            rpt.close();
        } catch (Exception e) {
            throw new ServletException("Failed to generate user list PDF", e);
        }
    }

    /** Renders one row and returns the next row number. */
    private int addRow(PdfPTable table, PdfReportBuilder rpt, int rowNum,
                       String username, String role, String currentUser) {
        boolean isCurrent = currentUser != null && currentUser.equalsIgnoreCase(username);
        String  display   = (isCurrent ? "* " : "") + safe(username);

        table.addCell(rpt.cell(String.valueOf(rowNum), isCurrent));
        table.addCell(rpt.cell(display, isCurrent));
        table.addCell(rpt.cell(role, isCurrent));
        return rowNum + 1;
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
