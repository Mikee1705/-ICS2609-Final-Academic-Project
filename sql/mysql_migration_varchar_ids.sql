-- ============================================================
-- MySQL Migration: align Student_ID / Teacher_ID with PostgreSQL
--
-- PostgreSQL uses VARCHAR(50) for these IDs. Previously MySQL used INT
-- — which happened to work because the seed IDs are all numeric strings,
-- but breaks the moment a non-numeric ID is used.
--
-- Run this against fap_activelearning ONCE to align both DBs.
-- ============================================================

USE fap_activelearning;

-- ----- Enrollments.Student_ID -----
ALTER TABLE Enrollments
    DROP INDEX uq_enrollment;
ALTER TABLE Enrollments
    MODIFY Student_ID VARCHAR(50) NOT NULL;
ALTER TABLE Enrollments
    ADD CONSTRAINT uq_enrollment UNIQUE (Student_ID, Course_ID);

-- ----- Teacher_Assignments.Teacher_ID -----
ALTER TABLE Teacher_Assignments
    DROP INDEX uq_teacher_course;
ALTER TABLE Teacher_Assignments
    MODIFY Teacher_ID VARCHAR(50) NOT NULL;
ALTER TABLE Teacher_Assignments
    ADD CONSTRAINT uq_teacher_course UNIQUE (Teacher_ID, Course_ID);

-- ----- Course_Ratings.Student_ID -----
ALTER TABLE Course_Ratings
    DROP INDEX uq_rating;
ALTER TABLE Course_Ratings
    MODIFY Student_ID VARCHAR(50) NOT NULL;
ALTER TABLE Course_Ratings
    ADD CONSTRAINT uq_rating UNIQUE (Student_ID, Course_ID);

-- ============================================================
-- VERIFY
-- ============================================================
-- DESCRIBE Enrollments;          -- Student_ID should now be varchar(50)
-- DESCRIBE Teacher_Assignments;  -- Teacher_ID should now be varchar(50)
-- DESCRIBE Course_Ratings;       -- Student_ID should now be varchar(50)
