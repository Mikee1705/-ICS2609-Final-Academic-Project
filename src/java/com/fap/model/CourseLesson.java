package com.fap.model;

import java.sql.Timestamp;

/**
 * Model: Course_Lessons table (fap_activelearning)
 */
public class CourseLesson {

    private int       lessonId;
    private int       courseId;
    private String    lessonTitle;
    private String    content;
    private String    lessonType;       // Video, Reading, Quiz, Activity
    private int       durationMinutes;
    private int       lessonOrder;
    private String    resourceUrl;
    private boolean   isPreview;        // true = free preview before enrollment
    private Timestamp createdAt;

    // Optional — populated by JOIN
    private String    courseName;

    public CourseLesson() {}

    // Getters & Setters
    public int getLessonId()                                  { return lessonId; }
    public void setLessonId(int lessonId)                     { this.lessonId = lessonId; }

    public int getCourseId()                                  { return courseId; }
    public void setCourseId(int courseId)                     { this.courseId = courseId; }

    public String getLessonTitle()                            { return lessonTitle; }
    public void setLessonTitle(String lessonTitle)            { this.lessonTitle = lessonTitle; }

    public String getContent()                                { return content; }
    public void setContent(String content)                    { this.content = content; }

    public String getLessonType()                             { return lessonType; }
    public void setLessonType(String lessonType)              { this.lessonType = lessonType; }

    public int getDurationMinutes()                           { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes)       { this.durationMinutes = durationMinutes; }

    public int getLessonOrder()                               { return lessonOrder; }
    public void setLessonOrder(int lessonOrder)               { this.lessonOrder = lessonOrder; }

    public String getResourceUrl()                            { return resourceUrl; }
    public void setResourceUrl(String resourceUrl)            { this.resourceUrl = resourceUrl; }

    public boolean isPreview()                                { return isPreview; }
    public void setPreview(boolean isPreview)                 { this.isPreview = isPreview; }

    public Timestamp getCreatedAt()                           { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)             { this.createdAt = createdAt; }

    public String getCourseName()                             { return courseName; }
    public void setCourseName(String courseName)              { this.courseName = courseName; }
}
