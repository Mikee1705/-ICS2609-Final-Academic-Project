package com.fap.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model: Enrollments table (fap_activelearning)
 * Enrollment_Date is the key field for time-bound report filtering.
 */
public class Enrollment {

    public enum Status { Active, Completed, Dropped }

    private int        enrollmentId;
    private int        studentId;
    private int        courseId;
    private Date       enrollmentDate;
    private Date       completionDate;
    private String     status;
    private BigDecimal progressPercent;  // 0.00 - 100.00
    private BigDecimal grade;
    private boolean    certificateIssued;
    private Timestamp  lastAccessed;

    // Optional — populated by JOIN
    private String     courseName;
    private String     studentName;  // from Derby/Postgres

    public Enrollment() {}

    // Getters & Setters
    public int getEnrollmentId()                                  { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId)                 { this.enrollmentId = enrollmentId; }

    public int getStudentId()                                     { return studentId; }
    public void setStudentId(int studentId)                       { this.studentId = studentId; }

    public int getCourseId()                                      { return courseId; }
    public void setCourseId(int courseId)                         { this.courseId = courseId; }

    public Date getEnrollmentDate()                               { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate)            { this.enrollmentDate = enrollmentDate; }

    public Date getCompletionDate()                               { return completionDate; }
    public void setCompletionDate(Date completionDate)            { this.completionDate = completionDate; }

    public String getStatus()                                     { return status; }
    public void setStatus(String status)                          { this.status = status; }

    public BigDecimal getProgressPercent()                        { return progressPercent; }
    public void setProgressPercent(BigDecimal progressPercent)    { this.progressPercent = progressPercent; }

    public BigDecimal getGrade()                                  { return grade; }
    public void setGrade(BigDecimal grade)                        { this.grade = grade; }

    public boolean isCertificateIssued()                          { return certificateIssued; }
    public void setCertificateIssued(boolean certificateIssued)   { this.certificateIssued = certificateIssued; }

    public Timestamp getLastAccessed()                            { return lastAccessed; }
    public void setLastAccessed(Timestamp lastAccessed)           { this.lastAccessed = lastAccessed; }

    public String getCourseName()                                 { return courseName; }
    public void setCourseName(String courseName)                  { this.courseName = courseName; }

    public String getStudentName()                                { return studentName; }
    public void setStudentName(String studentName)                { this.studentName = studentName; }
}
