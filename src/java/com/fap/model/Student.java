package com.fap.model;

import java.sql.Date;
import java.util.List;

/**
 * Model: Students table (PostgreSQL — user profile layer)
 *
 * Schema:
 *   Student_ID         VARCHAR(50) PK
 *   Salutation_ID      INT  → Salutations.Salutation_ID
 *   First_Name         VARCHAR(100)
 *   Last_Name          VARCHAR(100)
 *   Email              VARCHAR(255) UNIQUE
 *   Funding            VARCHAR(100)   — 'Self-Funded' / 'Scholarship' / 'Company-Paid'
 *   Registration_Date  DATE
 *   Username           VARCHAR(50) UNIQUE — added migration: bridges Derby & Postgres
 */
public class Student {

    private String studentId;
    private Integer salutationId;
    private String firstName;
    private String lastName;
    private String email;
    private String funding;
    private Date   registrationDate;
    private String username;

    // Optional — populated by JOINs
    private String salutationTitle;
    private List<StudentPhone> phones;

    public Student() {}

    // Getters & Setters
    public String getStudentId()                  { return studentId; }
    public void setStudentId(String s)            { this.studentId = s; }

    public Integer getSalutationId()              { return salutationId; }
    public void setSalutationId(Integer i)        { this.salutationId = i; }

    public String getFirstName()                  { return firstName; }
    public void setFirstName(String s)            { this.firstName = s; }

    public String getLastName()                   { return lastName; }
    public void setLastName(String s)             { this.lastName = s; }

    public String getEmail()                      { return email; }
    public void setEmail(String s)                { this.email = s; }

    public String getFunding()                    { return funding; }
    public void setFunding(String s)              { this.funding = s; }

    public Date getRegistrationDate()             { return registrationDate; }
    public void setRegistrationDate(Date d)       { this.registrationDate = d; }

    public String getUsername()                   { return username; }
    public void setUsername(String s)             { this.username = s; }

    public String getSalutationTitle()            { return salutationTitle; }
    public void setSalutationTitle(String s)      { this.salutationTitle = s; }

    public List<StudentPhone> getPhones()         { return phones; }
    public void setPhones(List<StudentPhone> p)   { this.phones = p; }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (salutationTitle != null && !salutationTitle.isEmpty()) sb.append(salutationTitle).append(' ');
        if (firstName != null)  sb.append(firstName);
        if (lastName  != null)  sb.append(sb.length() > 0 ? " " : "").append(lastName);
        return sb.toString().trim();
    }
}
