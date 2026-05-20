package com.fap.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Model: Courses table (fap_activelearning)
 */
public class Course {

    private int       courseId;
    private String    courseCode;
    private String    courseName;
    private String    description;
    private String    category;
    private String    level;           // Beginner, Intermediate, Advanced
    private int       durationHours;
    private int       maxStudents;
    private String    thumbnailUrl;
    private boolean   isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional — populated by JOIN queries when needed
    private List<CourseLesson>     lessons;
    private List<CourseAssignment> assignments;

    public Course() {}

    public Course(int courseId, String courseCode, String courseName, String description) {
        this.courseId    = courseId;
        this.courseCode  = courseCode;
        this.courseName  = courseName;
        this.description = description;
    }

    // Getters & Setters
    public int getCourseId()                                      { return courseId; }
    public void setCourseId(int courseId)                         { this.courseId = courseId; }

    public String getCourseCode()                                 { return courseCode; }
    public void setCourseCode(String courseCode)                  { this.courseCode = courseCode; }

    public String getCourseName()                                 { return courseName; }
    public void setCourseName(String courseName)                  { this.courseName = courseName; }

    public String getDescription()                                { return description; }
    public void setDescription(String description)                { this.description = description; }

    public String getCategory()                                   { return category; }
    public void setCategory(String category)                      { this.category = category; }

    public String getLevel()                                      { return level; }
    public void setLevel(String level)                            { this.level = level; }

    public int getDurationHours()                                 { return durationHours; }
    public void setDurationHours(int durationHours)               { this.durationHours = durationHours; }

    public int getMaxStudents()                                   { return maxStudents; }
    public void setMaxStudents(int maxStudents)                   { this.maxStudents = maxStudents; }

    public String getThumbnailUrl()                               { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl)              { this.thumbnailUrl = thumbnailUrl; }

    public boolean isActive()                                     { return isActive; }
    public void setActive(boolean isActive)                       { this.isActive = isActive; }

    public Timestamp getCreatedAt()                               { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)                 { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                               { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)                 { this.updatedAt = updatedAt; }

    public List<CourseLesson> getLessons()                        { return lessons; }
    public void setLessons(List<CourseLesson> lessons)            { this.lessons = lessons; }

    public List<CourseAssignment> getAssignments()                       { return assignments; }
    public void setAssignments(List<CourseAssignment> assignments)        { this.assignments = assignments; }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", code='" + courseCode + "', name='" + courseName + "'}";
    }
}
