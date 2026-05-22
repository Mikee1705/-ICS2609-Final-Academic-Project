package com.fap.report;

import com.fap.dao.derby.UserDAO;
import com.fap.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
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
 * Requirements covered:
 *   - Lists ALL users with role (no passwords printed)
 *   - Asterisk (*) beside the name of the currently logged-in admin
 *   - Landscape, paginated, with headers/footers from web.xml
 *   - Filename = USERLIST_<timestamp>.pdf
 *   - Client-side download (Content-Disposition: attachment)
 *
 * URL:
 *   /report/users
 */
@WebServlet("/report/users")
public class UserListReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Pull the logged-in user from the session
        HttpSession session = req.getSession(false);
        User loggedIn = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedIn == null || !"Admin".equalsIgnoreCase(loggedIn.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only admins can generate the user list report.");
            return;
        }

        try {
            UserDAO dao = new UserDAO(getServletContext());
            List<User> users = dao.getAllUsers();

            PdfReportBuilder rpt = new PdfReportBuilder(
                    getServletContext(), resp, "USERLIST", loggedIn.getUsername());
            Document doc = rpt.startDocument();

            doc.add(rpt.title("User List Report"));
            doc.add(rpt.subtitle("Total users: " + users.size()));

            // Columns: # | User ID | Username | Full Name | Email | Role | Status
            PdfPTable table = rpt.createTable(
                    new float[]{0.6f, 1f, 1.5f, 2.5f, 2.5f, 1.2f, 1f},
                    new String[]{"#", "User ID", "Username", "Full Name", "Email", "Role", "Status"});

            int rowNum = 1;
            for (User u : users) {
                boolean isCurrent = u.getUserId() == loggedIn.getUserId();
                String  fullName  = (isCurrent ? "* " : "") + safe(u.getFullName());

                table.addCell(rpt.cell(String.valueOf(rowNum++), isCurrent));
                table.addCell(rpt.cell(String.valueOf(u.getUserId()), isCurrent));
                table.addCell(rpt.cell(safe(u.getUsername()), isCurrent));
                table.addCell(rpt.cell(fullName, isCurrent));
                table.addCell(rpt.cell(safe(u.getEmail()), isCurrent));
                table.addCell(rpt.cell(safe(u.getRole()), isCurrent));
                table.addCell(rpt.cell(u.isActive() ? "Active" : "Inactive", isCurrent));
                // NOTE: passwordHash is intentionally NOT included
            }
            doc.add(table);

            // Legend
            PdfPCell legend = new PdfPCell(new com.itextpdf.text.Phrase(
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
