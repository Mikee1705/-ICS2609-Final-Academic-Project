package com.fap.report;

import com.fap.dao.derby.UserDAO;
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
 * Spec compliance:
 *   • At least 50 records (USERS seed has 55)
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
            UserDAO dao = new UserDAO(getServletContext());
            List<User> users = dao.getAllUsers();

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "USERLIST", me);
            Document doc = rpt.startDocument();

            doc.add(rpt.title("User List Report"));
            doc.add(rpt.subtitle("Total users: " + users.size()));

            // Columns: # | Username | Role
            PdfPTable table = rpt.createTable(
                    new float[]{0.8f, 4f, 2f},
                    new String[]{"#", "Username", "Role"});

            int rowNum = 1;
            for (User u : users) {
                boolean isCurrent = me != null && me.equalsIgnoreCase(u.getUsername());
                String  display   = (isCurrent ? "* " : "") + safe(u.getUsername());

                table.addCell(rpt.cell(String.valueOf(rowNum++), isCurrent));
                table.addCell(rpt.cell(display, isCurrent));
                table.addCell(rpt.cell(safe(u.getRole()), isCurrent));
                // NOTE: password is intentionally NOT included
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

    private static String safe(String s) { return s == null ? "" : s; }
}
