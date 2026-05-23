package com.fap.dao.postgres;

import com.fap.db.PostgresConnection;
import com.fap.model.Salutation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * SalutationDAO — PostgreSQL (public.Salutations)
 *
 * Lookup table for dropdowns (Mr. / Ms. / Mrs. / Dr. / Prof.).
 */
public class SalutationDAO {

    private final ServletContext context;

    public SalutationDAO(ServletContext context) {
        this.context = context;
    }

    public List<Salutation> getAllSalutations() throws SQLException {
        List<Salutation> list = new ArrayList<>();
        String sql = "SELECT * FROM Salutations ORDER BY Salutation_ID";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Salutation(
                        rs.getInt("Salutation_ID"),
                        rs.getString("Title")));
            }
        }
        return list;
    }
}
