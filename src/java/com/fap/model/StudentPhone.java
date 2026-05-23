package com.fap.model;

/**
 * Model: Student_Phones table (PostgreSQL)
 *
 * Schema:
 *   Phone_ID      SERIAL PK
 *   Student_ID    VARCHAR(50) FK → Students.Student_ID  (CASCADE delete)
 *   Phone_Number  VARCHAR(20)
 *   Phone_Type    VARCHAR(20)  (e.g. 'Mobile', 'Landline')
 *   UNIQUE (Student_ID, Phone_Number)
 */
public class StudentPhone {

    private int    phoneId;
    private String studentId;
    private String phoneNumber;
    private String phoneType;

    public StudentPhone() {}

    public StudentPhone(String studentId, String phoneNumber, String phoneType) {
        this.studentId  = studentId;
        this.phoneNumber = phoneNumber;
        this.phoneType  = phoneType;
    }

    public int getPhoneId()                       { return phoneId; }
    public void setPhoneId(int i)                 { this.phoneId = i; }

    public String getStudentId()                  { return studentId; }
    public void setStudentId(String s)            { this.studentId = s; }

    public String getPhoneNumber()                { return phoneNumber; }
    public void setPhoneNumber(String s)          { this.phoneNumber = s; }

    public String getPhoneType()                  { return phoneType; }
    public void setPhoneType(String s)            { this.phoneType = s; }
}
