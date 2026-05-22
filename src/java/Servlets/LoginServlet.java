package Servlets;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 *
 * @author juliojosechavez
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    String driver, username, password, url;
    String ClientUN, ClientPW, ClientRole;
    String status = "An Error Occured";
    Map<String, User> UList = new HashMap<>();

    // Catch and display database errors on the webpage
    String dbError = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Refresh the database records every time someone attempts to log in
        getRecords();

        if (!dbError.isEmpty()) {
            request.getSession().setAttribute("Title", "User Not Found");
            request.getSession().setAttribute("Error", dbError);
            response.sendRedirect("LoginError.jsp");
            return;
        }

        if (UList.isEmpty()) {
            request.getSession().setAttribute("Title", "User Not Found");
            request.getSession().setAttribute("Error", "The database connected successfully, but the USERS table is totally empty! Make sure you ran the INSERT statements.");
            response.sendRedirect("LoginError.jsp");
            return;
        }

        ClientUN = request.getParameter("clientUN");
        ClientPW = request.getParameter("clientPW");

        if (ClientUN != null) {
            ClientUN = ClientUN.trim();
        }
        if (ClientPW != null) {
            ClientPW = ClientPW.trim();
        }

        if (ClientUN == null || ClientUN.isEmpty() || ClientPW == null || ClientPW.isEmpty()) {
            request.getSession().setAttribute("Title", "Empty Fields");
            request.getSession().setAttribute("Error", "Empty Username or Password");
            response.sendRedirect("LoginError.jsp"); // Redirects to Empty Fields Error
        } else {
            if (UList.containsKey(ClientUN)) {
                User u = UList.get(ClientUN);
                if (ClientPW.equals(u.Password)) {
                    ClientRole = u.Role;

                    if (ClientRole.equalsIgnoreCase("Admin")) {
                        request.getSession().setAttribute("UName", ClientUN);
                        request.getSession().setAttribute("Role", ClientRole);

                        // Route through the AnalyticsServlet so the JSP receives real chart data
                        response.sendRedirect("Analytics");
                    } else {
                        request.getSession().setAttribute("Title", "Invalid Credentials");
                        request.getSession().setAttribute("Error", "Invalid Role");
                        response.sendRedirect("LoginError.jsp"); // Redirects to Invalid Credentials Error
                    }

                } else {
                    request.getSession().setAttribute("Title", "Invalid Credentials");
                    request.getSession().setAttribute("Error", "Incorrect Password");
                    response.sendRedirect("LoginError.jsp"); // Redirects to Invalid Credentials Error
                }
            } else {
                request.getSession().setAttribute("Title", "User Not Found");
                request.getSession().setAttribute("Error", "Username Does Not Exist");
                response.sendRedirect("LoginError.jsp");  // Redirects to User Not Found Error
            }
        }
    }

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        try {
            driver = context.getInitParameter("driver");
            Class.forName(driver);
            username = context.getInitParameter("dbUserName");
            password = context.getInitParameter("dbPassword");
            StringBuffer u = new StringBuffer(context.getInitParameter("jdbcDriverURL"))
                    .append("://")
                    .append(context.getInitParameter("dbHostName"))
                    .append(":")
                    .append(context.getInitParameter("dbPort"))
                    .append("/")
                    .append(context.getInitParameter("databaseName"));

            url = u.toString();
            getRecords();
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occured - "
                    + nfe.getMessage());
        }
    }

    //For Hashmap LoginServlet
    public void getRecords() {
        UList.clear();
        dbError = ""; // Reset error message on every attempt

        try (Connection conn = DriverManager.getConnection(url, this.username, this.password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM USERS ORDER BY USERROLE")) {

            while (rs.next()) {
                // Trim the database results just in case there are spaces saved in the DB
                User u = new User(
                        rs.getString("Username").trim(),
                        Security.Decrypt(this.getServletContext(), rs.getString("PASSWORD").trim()),
                        rs.getString("UserRole").trim()
                );
                UList.put(u.Username, u);
            }
        } catch (SQLException e) {
            // Capture the EXACT database error, credentials, and the URL it tried to use
            dbError = "DB Connection Failed: " + e.getMessage() + " | URL: " + url + " | User: " + this.username;
            System.err.println(dbError);
        }
    }
}
