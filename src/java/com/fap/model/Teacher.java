package com.fap.model;

/**
 * Model: Teachers table (PostgreSQL — user profile layer)
 *
 * Schema:
 *   Teacher_ID    VARCHAR(50) PK
 *   Salutation_ID INT  → Salutations.Salutation_ID
 *   First_Name    VARCHAR(100)
 *   Last_Name     VARCHAR(100)
 *   Username      VARCHAR(50) UNIQUE — added migration: bridges Derby & Postgres
 */
public class Teacher {

    private String  teacherId;
    private Integer salutationId;
    private String  firstName;
    private String  lastName;
    private String  username;

    // Optional — populated by JOIN
    private String  salutationTitle;

    public Teacher() {}

    public String getTeacherId()                  { return teacherId; }
    public void setTeacherId(String s)            { this.teacherId = s; }

    public Integer getSalutationId()              { return salutationId; }
    public void setSalutationId(Integer i)        { this.salutationId = i; }

    public String getFirstName()                  { return firstName; }
    public void setFirstName(String s)            { this.firstName = s; }

    public String getLastName()                   { return lastName; }
    public void setLastName(String s)             { this.lastName = s; }

    public String getUsername()                   { return username; }
    public void setUsername(String s)             { this.username = s; }

    public String getSalutationTitle()            { return salutationTitle; }
    public void setSalutationTitle(String s)      { this.salutationTitle = s; }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (salutationTitle != null && !salutationTitle.isEmpty()) sb.append(salutationTitle).append(' ');
        if (firstName != null)  sb.append(firstName);
        if (lastName  != null)  sb.append(sb.length() > 0 ? " " : "").append(lastName);
        return sb.toString().trim();
    }
}
