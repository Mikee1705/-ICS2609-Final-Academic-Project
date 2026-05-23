package com.fap.model;

import java.sql.Date;

/**
 * Model: Teacher_Assignments table (fap_activelearning)
 */
public class TeacherAssignment {

    private int     assignmentId;
    private String  teacherId;     // VARCHAR(50) — matches Postgres Teachers.Teacher_ID
    private int     courseId;
    private String  role;          // Primary, Co-Instructor, Teaching Assistant
    private Date    assignedDate;
    private Date    endDate;       // NULL = ongoing
    private boolean isActive;

    // Optional — populated by JOIN
    private String  courseName;
    private String  teacherName;  // from Derby/Postgres

    public TeacherAssignment() {}

    // Getters & Setters
    public int getAssignmentId()                              { return assignmentId; }
    public void setAssignmentId(int assignmentId)             { this.assignmentId = assignmentId; }

    public String getTeacherId()                              { return teacherId; }
    public void setTeacherId(String teacherId)                { this.teacherId = teacherId; }

    public int getCourseId()                                  { return courseId; }
    public void setCourseId(int courseId)                     { this.courseId = courseId; }

    public String getRole()                                   { return role; }
    public void setRole(String role)                          { this.role = role; }

    public Date getAssignedDate()                             { return assignedDate; }
    public void setAssignedDate(Date assignedDate)            { this.assignedDate = assignedDate; }

    public Date getEndDate()                                  { return endDate; }
    public void setEndDate(Date endDate)                      { this.endDate = endDate; }

    public boolean isActive()                                 { return isActive; }
    public void setActive(boolean isActive)                   { this.isActive = isActive; }

    public String getCourseName()                             { return courseName; }
    public void setCourseName(String courseName)              { this.courseName = courseName; }

    public String getTeacherName()                            { return teacherName; }
    public void setTeacherName(String teacherName)            { this.teacherName = teacherName; }
}
