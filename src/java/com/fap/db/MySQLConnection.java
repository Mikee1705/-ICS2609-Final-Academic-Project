package com.fap.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 * MySQLConnection
 *
 * Utility class for obtaining a MySQL JDBC connection.
 * Credentials are read from web.xml context-params (as required by the DD).
 *
 * Usage:
 *   Connection conn = MySQLConnection.getConnection(getServletContext());
 */
public class MySQLConnection {

    /**
     * Returns a MySQL connection using credentials from the deployment descriptor.
     *
     * @param context the ServletContext (used to read web.xml context-params)
     * @return a live JDBC Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection(ServletContext context) throws SQLException {
        String driver   = context.getInitParameter("mysql.driver");
        String url      = context.getInitParameter("mysql.url");
        String username = context.getInitParameter("mysql.username");
        String password = context.getInitParameter("mysql.password");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                    + "Add mysql-connector-java.jar to WEB-INF/lib.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Safely closes a connection without throwing.
     * Always call this in a finally block or try-with-resources.
     *
     * @param conn the connection to close (can be null)
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[MySQLConnection] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
