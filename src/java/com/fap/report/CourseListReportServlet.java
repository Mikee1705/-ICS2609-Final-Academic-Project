package com.fap.report;

import com.fap.dao.mysql.CourseDAO;
import com.fap.model.Course;
import com.fap.model.User;
import com.fap.util.DateRangeParser;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * CourseListReportServlet
 *
 * Generates  COURSELIST_yyyyMMddHHmmss.pdf
 *
 * URL params (all optional):
 *   ?scope=all       → ALL courses (admin default)
 *   ?scope=mine      → only courses where the logged-in user is the creator/teacher
 *   ?from=YYYY-MM-DD&to=YYYY-MM-DD → time-bound (uses Created_At)
 *
 * URL:  /report/courses
 */
@WebServlet("/report/courses")
public class CourseListReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String me   = (session != null) ? (String) session.getAttribute("UName") : null;
        String role = (session != null) ? (String) session.getAttribute("Role")  : null;
        if (me == null || role == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please log in.");
            return;
        }
        User loggedIn = new User(me, null, role);

        String scope = req.getParameter("scope");
        if (scope == null) scope = "all";

        DateRangeParser range = new DateRangeParser(req);
        boolean useDateRange = req.getParameter("from") != null || req.getParameter("to") != null;

        try {
            CourseDAO dao = new CourseDAO(getServletContext());

            List<Course> courses;
            if (useDateRange) {
                courses = dao.getCourseReportByDateRange(range.getFrom(), range.getTo());
            } else {
                courses = dao.getAllCourses();
            }

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "COURSELIST", loggedIn.getUsername());
            Document doc = rpt.startDocument();

            doc.add(rpt.title("Course List Report"));
            if (useDateRange) {
                doc.add(rpt.subtitle(range.describe()));
            } else {
                doc.add(rpt.subtitle("All Records"));
            }

            // Columns: # | Code | Name | Category | Level | Hours | Max Students | Status
            PdfPTable table = rpt.createTable(
                    new float[]{0.5f, 1.0f, 3f, 1.5f, 1.3f, 0.9f, 1.2f, 0.9f},
                    new String[]{"#", "Code", "Course Name", "Category", "Level", "Hours", "Max Students", "Status"});

            int rowNum = 1;
            for (Course c : courses) {
                table.addCell(rpt.cell(String.valueOf(rowNum++)));
                table.addCell(rpt.cell(safe(c.getCourseCode())));
                table.addCell(rpt.cell(safe(c.getCourseName())));
                table.addCell(rpt.cell(safe(c.getCategory())));
                table.addCell(rpt.cell(safe(c.getLevel())));
                table.addCell(rpt.cell(String.valueOf(c.getDurationHours())));
                table.addCell(rpt.cell(String.valueOf(c.getMaxStudents())));
                table.addCell(rpt.cell(c.isActive() ? "Active" : "Inactive"));
            }
            doc.add(table);

            rpt.close();
        } catch (Exception e) {
            throw new ServletException("Failed to generate course list PDF", e);
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
