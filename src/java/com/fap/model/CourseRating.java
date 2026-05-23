package com.fap.model;

import java.sql.Timestamp;

/**
 * Model: Course_Ratings table (fap_activelearning)
 * Rating_Score must be between 1 and 5.
 */
public class CourseRating {

    private int       ratingId;
    private String    studentId;        // VARCHAR(50) — matches Postgres Students.Student_ID
    private int       courseId;
    private int       ratingScore;      // 1–5
    private String    reviewText;       // Optional written review
    private Boolean   wouldRecommend;   // true = Yes, false = No, null = not answered
    private boolean   isVerified;       // true = student was enrolled when rating
    private Timestamp ratedAt;

    // Optional — populated by JOIN
    private String    courseName;
    private String    studentName;  // from Derby/Postgres

    public CourseRating() {}

    // Getters & Setters
    public int getRatingId()                                  { return ratingId; }
    public void setRatingId(int ratingId)                     { this.ratingId = ratingId; }

    public String getStudentId()                              { return studentId; }
    public void setStudentId(String studentId)                { this.studentId = studentId; }

    public int getCourseId()                                  { return courseId; }
    public void setCourseId(int courseId)                     { this.courseId = courseId; }

    public int getRatingScore()                               { return ratingScore; }
    public void setRatingScore(int ratingScore) {
        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }
        this.ratingScore = ratingScore;
    }

    public String getReviewText()                             { return reviewText; }
    public void setReviewText(String reviewText)              { this.reviewText = reviewText; }

    public Boolean getWouldRecommend()                        { return wouldRecommend; }
    public void setWouldRecommend(Boolean wouldRecommend)     { this.wouldRecommend = wouldRecommend; }

    public boolean isVerified()                               { return isVerified; }
    public void setVerified(boolean isVerified)               { this.isVerified = isVerified; }

    public Timestamp getRatedAt()                             { return ratedAt; }
    public void setRatedAt(Timestamp ratedAt)                 { this.ratedAt = ratedAt; }

    public String getCourseName()                             { return courseName; }
    public void setCourseName(String courseName)              { this.courseName = courseName; }

    public String getStudentName()                            { return studentName; }
    public void setStudentName(String studentName)            { this.studentName = studentName; }
}
