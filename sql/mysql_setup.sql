-- ============================================================
-- FAP MySQL Setup Script
-- DBMS 3: Active Learning, Inc. — Course Management
-- Run this entire script in NetBeans: right-click connection
-- → Execute Command → paste → Run (Ctrl+Shift+F9)
-- ============================================================

USE fap_activelearning;

-- ============================================================
-- TABLE 1: Courses
-- Core course catalog for Active Learning, Inc.
-- ============================================================
CREATE TABLE IF NOT EXISTS Courses (
    Course_ID       INT             AUTO_INCREMENT PRIMARY KEY,
    Course_Code     VARCHAR(20)     NOT NULL UNIQUE,
    Course_Name     VARCHAR(150)    NOT NULL,
    Description     TEXT,
    Category        VARCHAR(50)     NOT NULL,
    Level           ENUM('Beginner','Intermediate','Advanced') NOT NULL DEFAULT 'Beginner',
    Duration_Hours  INT             NOT NULL DEFAULT 0,
    Max_Students    INT             NOT NULL DEFAULT 30,
    Thumbnail_URL   VARCHAR(500)    NULL,
    Is_Active       TINYINT(1)      NOT NULL DEFAULT 1,
    Created_At      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    Updated_At      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 2: Course_Assignments
-- Assignments linked to a course via FK
-- ============================================================
CREATE TABLE IF NOT EXISTS Course_Assignments (
    Assignment_ID   INT             AUTO_INCREMENT PRIMARY KEY,
    Course_ID       INT             NOT NULL,
    Title           VARCHAR(200)    NOT NULL,
    Instructions    TEXT,
    Assignment_Type ENUM('Quiz','Project','Essay','Lab','Exam') NOT NULL DEFAULT 'Project',
    Due_Date        DATE            NOT NULL,
    Max_Score       DECIMAL(5,2)    NOT NULL DEFAULT 100.00,
    Passing_Score   DECIMAL(5,2)    NOT NULL DEFAULT 60.00,
    Is_Required     TINYINT(1)      NOT NULL DEFAULT 1,
    Created_At      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ca_course FOREIGN KEY (Course_ID)
        REFERENCES Courses(Course_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ============================================================
-- TABLE 3: Course_Lessons
-- Lessons/modules linked to a course via FK
-- ============================================================
CREATE TABLE IF NOT EXISTS Course_Lessons (
    Lesson_ID           INT             AUTO_INCREMENT PRIMARY KEY,
    Course_ID           INT             NOT NULL,
    Lesson_Title        VARCHAR(200)    NOT NULL,
    Content             TEXT,
    Lesson_Type         ENUM('Video','Reading','Quiz','Activity') NOT NULL DEFAULT 'Reading',
    Duration_Minutes    INT             NOT NULL DEFAULT 0,
    Lesson_Order        INT             NOT NULL DEFAULT 1,
    Resource_URL        VARCHAR(500)    NULL,
    Is_Preview          TINYINT(1)      NOT NULL DEFAULT 0,
    Created_At          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cl_course FOREIGN KEY (Course_ID)
        REFERENCES Courses(Course_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ============================================================
-- TABLE 4: Enrollments (Junction Table - 3NF)
-- Tracks which student is enrolled in which course.
-- Enrollment_Date is the key field for time-bound reports.
-- ============================================================
CREATE TABLE IF NOT EXISTS Enrollments (
    Enrollment_ID       INT             AUTO_INCREMENT PRIMARY KEY,
    Student_ID          INT             NOT NULL,
    Course_ID           INT             NOT NULL,
    Enrollment_Date     DATE            NOT NULL DEFAULT (CURRENT_DATE),
    Completion_Date     DATE            NULL,
    Status              ENUM('Active','Completed','Dropped') NOT NULL DEFAULT 'Active',
    Progress_Percent    DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    Grade               DECIMAL(5,2)    NULL,
    Certificate_Issued  TINYINT(1)      NOT NULL DEFAULT 0,
    Last_Accessed       TIMESTAMP       NULL,
    CONSTRAINT fk_en_course FOREIGN KEY (Course_ID)
        REFERENCES Courses(Course_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_enrollment UNIQUE (Student_ID, Course_ID)
);

-- ============================================================
-- TABLE 5: Teacher_Assignments (Junction Table - 3NF)
-- Tracks which teacher is assigned to which course.
-- ============================================================
CREATE TABLE IF NOT EXISTS Teacher_Assignments (
    Assignment_ID   INT             AUTO_INCREMENT PRIMARY KEY,
    Teacher_ID      INT             NOT NULL,
    Course_ID       INT             NOT NULL,
    Role            ENUM('Primary','Co-Instructor','Teaching Assistant') NOT NULL DEFAULT 'Primary',
    Assigned_Date   DATE            NOT NULL DEFAULT (CURRENT_DATE),
    End_Date        DATE            NULL,
    Is_Active       TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT fk_ta_course FOREIGN KEY (Course_ID)
        REFERENCES Courses(Course_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_teacher_course UNIQUE (Teacher_ID, Course_ID)
);

-- ============================================================
-- TABLE 6: Course_Ratings (Junction Table - 3NF)
-- Student ratings per course (1-5 scale).
-- Rated_At is the key field for time-bound reports.
-- ============================================================
CREATE TABLE IF NOT EXISTS Course_Ratings (
    Rating_ID           INT             AUTO_INCREMENT PRIMARY KEY,
    Student_ID          INT             NOT NULL,
    Course_ID           INT             NOT NULL,
    Rating_Score        TINYINT         NOT NULL,
    Review_Text         TEXT            NULL,
    Would_Recommend     TINYINT(1)      NULL,
    Is_Verified         TINYINT(1)      NOT NULL DEFAULT 0,
    Rated_At            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cr_course FOREIGN KEY (Course_ID)
        REFERENCES Courses(Course_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_rating CHECK (Rating_Score BETWEEN 1 AND 5),
    CONSTRAINT uq_rating UNIQUE (Student_ID, Course_ID)
);

-- ============================================================
-- SAMPLE DATA
-- Provides enough records for report generation and testing
-- ============================================================

INSERT INTO Courses (Course_Code, Course_Name, Description, Category, Level, Duration_Hours, Max_Students, Is_Active) VALUES
('WD101',  'Introduction to Web Development',  'Fundamentals of HTML, CSS, and JavaScript for beginners.',          'Programming',  'Beginner',     40,  30, 1),
('JP201',  'Java Programming Essentials',       'Core Java concepts including OOP, collections, and exceptions.',    'Programming',  'Intermediate', 60,  25, 1),
('DB301',  'Database Management Systems',       'Relational databases, SQL, normalization, and transactions.',       'Database',     'Intermediate', 45,  30, 1),
('UX401',  'UI/UX Design Principles',           'User-centered design, wireframing, and prototyping techniques.',   'Design',       'Beginner',     35,  20, 1),
('MA501',  'Mobile App Development',            'Cross-platform mobile development using modern frameworks.',        'Programming',  'Advanced',     80,  20, 1);

INSERT INTO Course_Lessons (Course_ID, Lesson_Title, Content, Lesson_Type, Duration_Minutes, Lesson_Order, Is_Preview) VALUES
(1, 'HTML Basics',          'Introduction to HTML tags and document structure.',        'Reading',  30,  1, 1),
(1, 'CSS Fundamentals',     'Styling with CSS: selectors, box model, and layouts.',     'Reading',  45,  2, 0),
(1, 'JavaScript Intro',     'Variables, functions, and DOM manipulation.',              'Video',    60,  3, 0),
(2, 'OOP in Java',          'Classes, objects, inheritance, and polymorphism.',         'Reading',  60,  1, 1),
(2, 'Java Collections',     'Lists, Maps, Sets, and iterators.',                        'Reading',  50,  2, 0),
(3, 'SQL Basics',           'SELECT, INSERT, UPDATE, DELETE statements.',               'Activity', 45,  1, 1),
(3, 'Normalization',        '1NF, 2NF, 3NF explained with practical examples.',        'Reading',  40,  2, 0);

INSERT INTO Course_Assignments (Course_ID, Title, Instructions, Assignment_Type, Due_Date, Max_Score, Passing_Score, Is_Required) VALUES
(1, 'Build a Portfolio Page',   'Create a personal portfolio using HTML and CSS.',            'Project',  '2026-06-01', 100, 60, 1),
(1, 'JS Calculator',            'Build a functional calculator using vanilla JavaScript.',    'Project',  '2026-06-15', 100, 60, 1),
(1, 'HTML Quiz',                'Answer 20 questions about HTML fundamentals.',               'Quiz',     '2026-05-20', 50,  30, 1),
(2, 'Bank Account OOP',         'Implement a BankAccount class with deposit/withdrawal.',     'Project',  '2026-06-10', 100, 60, 1),
(2, 'Collections Lab',          'Practice using ArrayList, HashMap and HashSet.',             'Lab',      '2026-06-05', 50,  30, 1),
(3, 'ER Diagram Exercise',      'Design an ER diagram for a library management system.',      'Project',  '2026-06-20', 50,  30, 1),
(3, 'SQL Final Exam',           'Written exam covering all SQL and normalization topics.',    'Exam',     '2026-06-25', 100, 60, 1);

INSERT INTO Enrollments (Student_ID, Course_ID, Enrollment_Date, Status, Progress_Percent, Grade, Certificate_Issued) VALUES
(1, 1, '2026-01-10', 'Active',    75.00, NULL,  0),
(1, 2, '2026-01-10', 'Active',    40.00, NULL,  0),
(2, 1, '2026-01-12', 'Completed', 100.00, 92.50, 1),
(2, 3, '2026-01-15', 'Active',    60.00, NULL,  0),
(3, 2, '2026-02-01', 'Dropped',   20.00, NULL,  0),
(4, 4, '2026-02-05', 'Active',    85.00, NULL,  0),
(5, 5, '2026-03-01', 'Active',    30.00, NULL,  0);

INSERT INTO Teacher_Assignments (Teacher_ID, Course_ID, Role, Assigned_Date, Is_Active) VALUES
(1, 1, 'Primary',           '2026-01-05', 1),
(1, 2, 'Primary',           '2026-01-05', 1),
(2, 3, 'Primary',           '2026-01-06', 1),
(2, 1, 'Co-Instructor',     '2026-01-06', 1),
(3, 4, 'Primary',           '2026-01-07', 1),
(3, 5, 'Primary',           '2026-01-08', 1);

INSERT INTO Course_Ratings (Student_ID, Course_ID, Rating_Score, Review_Text, Would_Recommend, Is_Verified) VALUES
(1, 1, 5, 'Very well structured and easy to follow!',          1, 1),
(1, 2, 4, 'Great content but could use more examples.',        1, 1),
(2, 1, 5, 'Best web dev course I have taken.',                 1, 1),
(2, 3, 3, 'Content is good but the pace is too fast.',         1, 1),
(4, 4, 4, 'Loved the wireframing section.',                    1, 1),
(5, 5, 5, 'Comprehensive and very practical.',                 1, 1);
