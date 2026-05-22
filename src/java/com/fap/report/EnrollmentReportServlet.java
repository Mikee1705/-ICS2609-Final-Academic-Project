package com.fap.report;

import com.fap.dao.mysql.EnrollmentDAO;
import com.fap.model.Enrollment;
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
 * EnrollmentReportServlet
 *
 * Generates  ENROLLMENTLIST_yyyyMMddHHmmss.pdf
 *
 * URL params:
 *   ?scope=all   → ALL enrollments (admin default)
 *   ?scope=mine  → only enrollments for the currently logged-in student
 *   ?from=YYYY-MM-DD&to=YYYY-MM-DD → time-bound by Enrollment_Date
 *
 * URL:  /report/enrollments
 */
@WebServlet("/report/enrollments")
public class EnrollmentReportServlet extends HttpServlet {

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
            EnrollmentDAO dao = new EnrollmentDAO(getServletContext());

            List<Enrollment> data;
            String subtitle;

            if ("mine".equalsIgnoreCase(scope)) {
                // "print only their records"
                if (useDateRange) {
                    data = dao.getEnrollmentsByStudentAndDateRange(
                            loggedIn.getUserId(), range.getFrom(), range.getTo());
                    subtitle = "My Enrollments — " + range.describe();
                } else {
                    data = dao.getEnrollmentsByStudent(loggedIn.getUserId());
                    subtitle = "My Enrollments — All Records";
                }
            } else {
                // ALL records (admin only)
                if (!"Admin".equalsIgnoreCase(loggedIn.getRole())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Only admins can view all enrollment records.");
                    return;
                }
                if (useDateRange) {
                    data = dao.getAllEnrollmentsByDateRange(range.getFrom(), range.getTo());
                    subtitle = "All Enrollments — " + range.describe();
                } else {
                    data = dao.getAllEnrollments();
                    subtitle = "All Enrollments — All Records";
                }
            }

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "ENROLLMENTLIST", loggedIn.getUsername());
            Document doc = rpt.startDocument();

            doc.add(rpt.title("Enrollment Report"));
            doc.add(rpt.subtitle(subtitle));

            // Columns: # | Enroll ID | Student ID | Course | Enroll Date | Status | Progress | Grade | Cert
            PdfPTable table = rpt.createTable(
                    new float[]{0.5f, 1f, 1f, 3f, 1.4f, 1.1f, 1.1f, 0.9f, 0.7f},
                    new String[]{"#", "Enroll ID", "Student ID", "Course", "Enroll Date",
                                 "Status", "Progress", "Grade", "Cert"});

            int rowNum = 1;
            for (Enrollment e : data) {
                table.addCell(rpt.cell(String.valueOf(rowNum++)));
                table.addCell(rpt.cell(String.valueOf(e.getEnrollmentId())));
                table.addCell(rpt.cell(String.valueOf(e.getStudentId())));
                table.addCell(rpt.cell(safe(e.getCourseName())));
                table.addCell(rpt.cell(e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : ""));
                table.addCell(rpt.cell(safe(e.getStatus())));
                table.addCell(rpt.cell(e.getProgressPercent() != null
                        ? e.getProgressPercent().toString() + "%" : "0%"));
                table.addCell(rpt.cell(e.getGrade() != null ? e.getGrade().toString() : "—"));
                table.addCell(rpt.cell(e.isCertificateIssued() ? "Yes" : "No"));
            }
            doc.add(table);

            rpt.close();
        } catch (Exception ex) {
            throw new ServletException("Failed to generate enrollment PDF", ex);
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
