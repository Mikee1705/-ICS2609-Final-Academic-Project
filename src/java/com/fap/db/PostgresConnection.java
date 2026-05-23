package com.fap.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 * PostgresConnection
 *
 * Utility class for obtaining a PostgreSQL JDBC connection.
 * Credentials are read from web.xml context-params (DBMS 3 — user profile layer).
 *
 * Usage:
 *   Connection conn = PostgresConnection.getConnection(getServletContext());
 */
public class PostgresConnection {

    /**
     * Returns a PostgreSQL connection using credentials from the deployment descriptor.
     *
     * @param context the ServletContext (used to read web.xml context-params)
     * @return a live JDBC Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection(ServletContext context) throws SQLException {
        String driver   = context.getInitParameter("postgres.driver");
        String url      = context.getInitParameter("postgres.url");
        String username = context.getInitParameter("postgres.username");
        String password = context.getInitParameter("postgres.password");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found. "
                    + "Add postgresql-*.jar to WEB-INF/lib.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    /** Safely close a connection without throwing. */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[PostgresConnection] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
