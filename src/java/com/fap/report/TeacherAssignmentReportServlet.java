package com.fap.report;

import com.fap.dao.mysql.TeacherAssignmentDAO;
import com.fap.model.TeacherAssignment;
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
 * TeacherAssignmentReportServlet
 *
 * Generates  TEACHERASSIGNMENTS_yyyyMMddHHmmss.pdf
 *
 * URL params:
 *   ?scope=all   → ALL teacher assignments (admin)
 *   ?scope=mine  → only the logged-in teacher's assignments
 *   ?from=YYYY-MM-DD&to=YYYY-MM-DD → time-bound by Assigned_Date
 *
 * URL:  /report/teacher-assignments
 */
@WebServlet("/report/teacher-assignments")
public class TeacherAssignmentReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User loggedIn = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedIn == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please log in.");
            return;
        }

        String scope = req.getParameter("scope");
        if (scope == null) scope = "all";

        DateRangeParser range = new DateRangeParser(req);
        boolean useDateRange = req.getParameter("from") != null || req.getParameter("to") != null;

        try {
            TeacherAssignmentDAO dao = new TeacherAssignmentDAO(getServletContext());

            List<TeacherAssignment> data;
            String subtitle;

            if ("mine".equalsIgnoreCase(scope)) {
                if (useDateRange) {
                    data = dao.getAssignmentsByTeacherAndDateRange(
                            loggedIn.getUserId(), range.getFrom(), range.getTo());
                    subtitle = "My Assignments — " + range.describe();
                } else {
                    data = dao.getAssignmentsByTeacher(loggedIn.getUserId());
                    subtitle = "My Assignments — All Records";
                }
            } else {
                if (!"Admin".equalsIgnoreCase(loggedIn.getRole())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Only admins can view all teacher assignment records.");
                    return;
                }
                if (useDateRange) {
                    data = dao.getAssignmentsByDateRange(range.getFrom(), range.getTo());
                    subtitle = "All Teacher Assignments — " + range.describe();
                } else {
                    data = dao.getAllTeacherAssignments();
                    subtitle = "All Teacher Assignments — All Records";
                }
            }

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "TEACHERASSIGNMENTS", loggedIn.getUsername());
            Document doc = rpt.startDocument();

            doc.add(rpt.title("Teacher Assignment Report"));
            doc.add(rpt.subtitle(subtitle));

            // Columns: # | Assignment ID | Teacher ID | Course | Role | Assigned | End | Active
            PdfPTable table = rpt.createTable(
                    new float[]{0.5f, 1.1f, 1.1f, 3f, 1.5f, 1.3f, 1.3f, 0.9f},
                    new String[]{"#", "Assignment ID", "Teacher ID", "Course", "Role",
                                 "Assigned Date", "End Date", "Active"});

            int rowNum = 1;
            for (TeacherAssignment ta : data) {
                table.addCell(rpt.cell(String.valueOf(rowNum++)));
                table.addCell(rpt.cell(String.valueOf(ta.getAssignmentId())));
                table.addCell(rpt.cell(String.valueOf(ta.getTeacherId())));
                table.addCell(rpt.cell(safe(ta.getCourseName())));
                table.addCell(rpt.cell(safe(ta.getRole())));
                table.addCell(rpt.cell(ta.getAssignedDate() != null ? ta.getAssignedDate().toString() : ""));
                table.addCell(rpt.cell(ta.getEndDate() != null ? ta.getEndDate().toString() : "Ongoing"));
                table.addCell(rpt.cell(ta.isActive() ? "Yes" : "No"));
            }
            doc.add(table);

            rpt.close();
        } catch (Exception ex) {
            throw new ServletException("Failed to generate teacher assignment PDF", ex);
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
