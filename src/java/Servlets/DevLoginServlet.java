package Servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * DevLoginServlet — DEVELOPMENT-ONLY shortcut.
 *
 * Bypasses Derby + reCAPTCHA so the team can preview the JSPs while the
 * real USERS table is still being built by another teammate.
 *
 * Visit:  http://localhost:8080/FAP/DevLogin
 *         → instantly logged in as the dummy admin "devadmin"
 *         → redirected to /Analytics
 *
 * REMOVE THIS SERVLET (and its @WebServlet mapping) before final submission.
 */
@WebServlet("/DevLogin")
public class DevLoginServlet extends HttpServlet {

    private static final String DUMMY_USERNAME = "devadmin";
    private static final String DUMMY_ROLE     = "Admin";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Set the same session attributes that the real LoginServlet would set
        HttpSession sess = req.getSession(true);
        sess.setAttribute("UName", DUMMY_USERNAME);
        sess.setAttribute("Role",  DUMMY_ROLE);

        // Send the dev user to the analytics dashboard
        resp.sendRedirect(req.getContextPath() + "/Analytics");
    }
}
