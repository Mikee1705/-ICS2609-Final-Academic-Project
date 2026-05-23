package com.fap.report;

import com.fap.dao.mysql.CourseRatingDAO;
import com.fap.dao.postgres.StudentDAO;
import com.fap.model.CourseRating;
import com.fap.model.Student;
import com.fap.model.User;
import com.fap.util.DateRangeParser;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * CourseRatingReportServlet
 *
 * Generates  COURSERATINGS_yyyyMMddHHmmss.pdf
 *
 * URL params:
 *   ?scope=all   → ALL ratings (admin)
 *   ?scope=mine  → only ratings submitted by the logged-in student
 *   ?from=YYYY-MM-DD&to=YYYY-MM-DD → time-bound by Rated_At
 *
 * URL:  /report/ratings
 */
@WebServlet("/report/ratings")
public class CourseRatingReportServlet extends HttpServlet {

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
            CourseRatingDAO dao = new CourseRatingDAO(getServletContext());

            List<CourseRating> data;
            String subtitle;

            if ("mine".equalsIgnoreCase(scope)) {
                // Identity bridge: Derby username → Postgres Student_ID
                Student profile = new StudentDAO(getServletContext())
                        .getStudentByUsername(loggedIn.getUsername());
                if (profile == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "No student profile found for the logged-in user.");
                    return;
                }
                data = dao.getRatingsByStudent(profile.getStudentId());
                subtitle = "My Ratings — All Records";
                // Time-bound filter applied in-memory since the DAO method takes Timestamp range only
                if (useDateRange) {
                    Timestamp from = new Timestamp(range.getFrom().getTime());
                    Timestamp to   = new Timestamp(range.getTo().getTime() + 86_399_000L);
                    data.removeIf(r -> r.getRatedAt() == null
                            || r.getRatedAt().before(from)
                            || r.getRatedAt().after(to));
                    subtitle = "My Ratings — " + range.describe();
                }
            } else {
                if (!"Admin".equalsIgnoreCase(loggedIn.getRole())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Only admins can view all rating records.");
                    return;
                }
                if (useDateRange) {
                    Timestamp from = new Timestamp(range.getFrom().getTime());
                    Timestamp to   = new Timestamp(range.getTo().getTime() + 86_399_000L);
                    data = dao.getRatingsByDateRange(from, to);
                    subtitle = "All Ratings — " + range.describe();
                } else {
                    data = dao.getAllRatings();
                    subtitle = "All Ratings — All Records";
                }
            }

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "COURSERATINGS", loggedIn.getUsername());
            Document doc = rpt.startDocument();

            doc.add(rpt.title("Course Ratings Report"));
            doc.add(rpt.subtitle(subtitle));

            // Columns: # | Rating ID | Student ID | Course | Score | Recommend | Verified | Rated At
            PdfPTable table = rpt.createTable(
                    new float[]{0.5f, 1.1f, 1.1f, 3f, 0.8f, 1.2f, 1.0f, 1.6f},
                    new String[]{"#", "Rating ID", "Student ID", "Course", "Score",
                                 "Recommend", "Verified", "Rated At"});

            int rowNum = 1;
            for (CourseRating r : data) {
                table.addCell(rpt.cell(String.valueOf(rowNum++)));
                table.addCell(rpt.cell(String.valueOf(r.getRatingId())));
                table.addCell(rpt.cell(String.valueOf(r.getStudentId())));
                table.addCell(rpt.cell(safe(r.getCourseName())));
                table.addCell(rpt.cell(r.getRatingScore() + " / 5"));
                table.addCell(rpt.cell(r.getWouldRecommend() == null
                        ? "—"
                        : (r.getWouldRecommend() ? "Yes" : "No")));
                table.addCell(rpt.cell(r.isVerified() ? "Yes" : "No"));
                table.addCell(rpt.cell(r.getRatedAt() != null ? r.getRatedAt().toString() : ""));
            }
            doc.add(table);

            rpt.close();
        } catch (Exception ex) {
            throw new ServletException("Failed to generate ratings PDF", ex);
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
