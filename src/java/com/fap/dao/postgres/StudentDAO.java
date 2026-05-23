package com.fap.dao.postgres;

import com.fap.db.PostgresConnection;
import com.fap.model.Student;
import com.fap.model.StudentPhone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * StudentDAO — PostgreSQL (public.Students + Student_Phones + Salutations)
 *
 * The Postgres "Username" column is the identity bridge between this DB
 * and Derby. See ARCHITECTURE.md for the cross-DBMS data flow.
 */
public class StudentDAO {

    private final ServletContext context;

    public StudentDAO(ServletContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /** All students, joined with their salutation title. */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, sal.Title AS Salutation_Title "
                   + "FROM Students s "
                   + "LEFT JOIN Salutations sal ON s.Salutation_ID = sal.Salutation_ID "
                   + "ORDER BY s.Last_Name, s.First_Name";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    /** Student by primary key. */
    public Student getStudentById(String studentId) throws SQLException {
        String sql = "SELECT s.*, sal.Title AS Salutation_Title "
                   + "FROM Students s "
                   + "LEFT JOIN Salutations sal ON s.Salutation_ID = sal.Salutation_ID "
                   + "WHERE s.Student_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        }
        return null;
    }

    /**
     * Identity bridge — find a Postgres student profile by their Derby USERNAME.
     * Returns null if no profile exists (e.g. user is an Admin).
     */
    public Student getStudentByUsername(String username) throws SQLException {
        String sql = "SELECT s.*, sal.Title AS Salutation_Title "
                   + "FROM Students s "
                   + "LEFT JOIN Salutations sal ON s.Salutation_ID = sal.Salutation_ID "
                   + "WHERE s.Username = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        }
        return null;
    }

    /** All phone numbers for a given student. */
    public List<StudentPhone> getPhonesForStudent(String studentId) throws SQLException {
        List<StudentPhone> list = new ArrayList<>();
        String sql = "SELECT * FROM Student_Phones WHERE Student_ID = ? ORDER BY Phone_Type";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPhone(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // WRITE
    // ----------------------------------------------------------------

    /**
     * Insert a new student profile. Caller is responsible for first creating
     * a matching Derby USERS row (so the Username FK lookup works).
     *
     * @return the generated Student_ID? No — Postgres Student_ID is VARCHAR and
     *         must be set by the caller. Returns true if exactly 1 row inserted.
     */
    public boolean insertStudent(Student s) throws SQLException {
        String sql = "INSERT INTO Students "
                   + "(Student_ID, Salutation_ID, First_Name, Last_Name, Email, Funding, Registration_Date, Username) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            if (s.getSalutationId() != null) ps.setInt(2, s.getSalutationId());
            else                              ps.setNull(2, Types.INTEGER);
            ps.setString(3, s.getFirstName());
            ps.setString(4, s.getLastName());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getFunding());
            ps.setDate(7, s.getRegistrationDate() != null
                    ? s.getRegistrationDate()
                    : new Date(System.currentTimeMillis()));
            ps.setString(8, s.getUsername());
            return ps.executeUpdate() == 1;
        }
    }

    /** Update an existing student. */
    public boolean updateStudent(Student s) throws SQLException {
        String sql = "UPDATE Students SET "
                   + "Salutation_ID = ?, First_Name = ?, Last_Name = ?, "
                   + "Email = ?, Funding = ? "
                   + "WHERE Student_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (s.getSalutationId() != null) ps.setInt(1, s.getSalutationId());
            else                              ps.setNull(1, Types.INTEGER);
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getFunding());
            ps.setString(6, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Delete a student (cascades to Student_Phones). */
    public boolean deleteStudent(String studentId) throws SQLException {
        String sql = "DELETE FROM Students WHERE Student_ID = ?";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Add a phone number for a student. */
    public boolean insertPhone(StudentPhone phone) throws SQLException {
        String sql = "INSERT INTO Student_Phones (Student_ID, Phone_Number, Phone_Type) "
                   + "VALUES (?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection(context);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone.getStudentId());
            ps.setString(2, phone.getPhoneNumber());
            ps.setString(3, phone.getPhoneType());
            return ps.executeUpdate() == 1;
        }
    }

    // ----------------------------------------------------------------
    // MAPPERS
    // ----------------------------------------------------------------

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getString("Student_ID"));
        int sid = rs.getInt("Salutation_ID");
        s.setSalutationId(rs.wasNull() ? null : sid);
        s.setFirstName(rs.getString("First_Name"));
        s.setLastName(rs.getString("Last_Name"));
        s.setEmail(rs.getString("Email"));
        s.setFunding(rs.getString("Funding"));
        s.setRegistrationDate(rs.getDate("Registration_Date"));
        s.setUsername(rs.getString("Username"));
        try { s.setSalutationTitle(rs.getString("Salutation_Title")); }
        catch (SQLException ignored) { /* column not present */ }
        return s;
    }

    private StudentPhone mapPhone(ResultSet rs) throws SQLException {
        StudentPhone p = new StudentPhone();
        p.setPhoneId(rs.getInt("Phone_ID"));
        p.setStudentId(rs.getString("Student_ID"));
        p.setPhoneNumber(rs.getString("Phone_Number"));
        p.setPhoneType(rs.getString("Phone_Type"));
        return p;
    }
}
