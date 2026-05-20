package com.fap.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model: Course_Assignments table (fap_activelearning)
 */
public class CourseAssignment {

    private int        assignmentId;
    private int        courseId;
    private String     title;
    private String     instructions;
    private String     assignmentType;  // Quiz, Project, Essay, Lab, Exam
    private Date       dueDate;
    private BigDecimal maxScore;
    private BigDecimal passingScore;
    private boolean    isRequired;
    private Timestamp  createdAt;

    // Optional — populated by JOIN
    private String     courseName;

    public CourseAssignment() {}

    // Getters & Setters
    public int getAssignmentId()                              { return assignmentId; }
    public void setAssignmentId(int assignmentId)             { this.assignmentId = assignmentId; }

    public int getCourseId()                                  { return courseId; }
    public void setCourseId(int courseId)                     { this.courseId = courseId; }

    public String getTitle()                                  { return title; }
    public void setTitle(String title)                        { this.title = title; }

    public String getInstructions()                           { return instructions; }
    public void setInstructions(String instructions)          { this.instructions = instructions; }

    public String getAssignmentType()                         { return assignmentType; }
    public void setAssignmentType(String assignmentType)      { this.assignmentType = assignmentType; }

    public Date getDueDate()                                  { return dueDate; }
    public void setDueDate(Date dueDate)                      { this.dueDate = dueDate; }

    public BigDecimal getMaxScore()                           { return maxScore; }
    public void setMaxScore(BigDecimal maxScore)              { this.maxScore = maxScore; }

    public BigDecimal getPassingScore()                       { return passingScore; }
    public void setPassingScore(BigDecimal passingScore)      { this.passingScore = passingScore; }

    public boolean isRequired()                               { return isRequired; }
    public void setRequired(boolean isRequired)               { this.isRequired = isRequired; }

    public Timestamp getCreatedAt()                           { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)             { this.createdAt = createdAt; }

    public String getCourseName()                             { return courseName; }
    public void setCourseName(String courseName)              { this.courseName = courseName; }
}
