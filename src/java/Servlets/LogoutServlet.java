package Servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * LogoutServlet
 *
 * Invalidates the session and redirects to the login page.
 * Endpoint:  GET /Logout
 */
@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sess = req.getSession(false);
        if (sess != null) sess.invalidate();

        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}
