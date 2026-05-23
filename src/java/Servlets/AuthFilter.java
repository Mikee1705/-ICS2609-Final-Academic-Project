package Servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthFilter — small helper that the new controller servlets call at the
 * top of every doGet/doPost.
 *
 * Mirrors the session contract set by LoginServlet:
 *   • Session attribute "UName"  → username string
 *   • Session attribute "Role"   → "Admin" / "Teacher" / "Student"
 *
 * Returns false if the request was already redirected/responded to.
 * Returns true if the caller should continue handling the request.
 */
public class AuthFilter {

    /** Require an authenticated session (any role). */
    public static boolean requireLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession sess = req.getSession(false);
        if (sess == null || sess.getAttribute("Role") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    /** Require an authenticated session with Admin role. */
    public static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!requireLogin(req, resp)) return false;
        String role = (String) req.getSession().getAttribute("Role");
        if (!"Admin".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    public static String currentUsername(HttpServletRequest req) {
        HttpSession sess = req.getSession(false);
        return (sess != null) ? (String) sess.getAttribute("UName") : null;
    }

    public static String currentRole(HttpServletRequest req) {
        HttpSession sess = req.getSession(false);
        return (sess != null) ? (String) sess.getAttribute("Role") : null;
    }
}
