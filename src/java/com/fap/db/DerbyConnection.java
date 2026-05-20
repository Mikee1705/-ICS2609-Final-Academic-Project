package com.fap.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 * DerbyConnection
 *
 * Utility class for obtaining a Derby JDBC connection.
 * Credentials are read from web.xml context-params (as required by the DD).
 *
 * Usage:
 *   Connection conn = DerbyConnection.getConnection(getServletContext());
 */
public class DerbyConnection {

    /**
     * Returns a Derby connection using credentials from the deployment descriptor.
     *
     * @param context the ServletContext (used to read web.xml context-params)
     * @return a live JDBC Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection(ServletContext context) throws SQLException {
        String driver   = context.getInitParameter("derby.driver");
        String url      = context.getInitParameter("derby.url");
        String username = context.getInitParameter("derby.username");
        String password = context.getInitParameter("derby.password");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Derby JDBC Driver not found. "
                    + "Ensure derbyclient.jar is in WEB-INF/lib.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Safely closes a connection without throwing.
     *
     * @param conn the connection to close (can be null)
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DerbyConnection] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
