-- ============================================================
-- FAP MySQL Seeder — Academic Data
-- ============================================================
-- Seeds the fap_activelearning DB with realistic Enrollments,
-- Teacher_Assignments, and Course_Ratings using IDs that EXIST in
-- the PostgreSQL profile DB.
--
-- ID convention (cross-DB):
--   Teacher_IDs:  '6' – '15'   (10 teachers in Postgres)
--   Student_IDs:  '16' – '55'  (40 students in Postgres)
--
-- PREREQUISITES:
--   1. fap_activelearning database exists with schema from mysql_setup.sql
--      (only the CREATE TABLE statements — seed data is overwritten by THIS file)
--   2. The 3 ID columns are VARCHAR(50) — run mysql_migration_varchar_ids.sql
--      first if you set up MySQL before the recent migration
--   3. PostgreSQL has the corresponding Students/Teachers rows
--      (cross-DBMS referential integrity is logical, not enforced)
--
-- USAGE:
--   Run this in NetBeans → MySQL connection → Execute Command.
--   It wipes ALL academic tables first, then re-seeds the entire DB
--   (Courses, Lessons, Assignments, Teacher_Assignments, Enrollments, Ratings).
--   Safe to run repeatedly.
-- ============================================================

USE fap_activelearning;

-- ----- Clean slate so the script is re-runnable -----
-- ON DELETE CASCADE means deleting Courses wipes the children too,
-- but we delete children first explicitly so it's clear what happens.
DELETE FROM Course_Ratings;
DELETE FROM Teacher_Assignments;
DELETE FROM Enrollments;
DELETE FROM Course_Assignments;
DELETE FROM Course_Lessons;
DELETE FROM Courses;
ALTER TABLE Courses AUTO_INCREMENT = 1;

-- ============================================================
-- COURSES
-- The 5 core courses every other table FKs to.
-- Course_IDs will auto-increment 1–5 in this exact order.
-- ============================================================
INSERT INTO Courses (Course_Code, Course_Name, Description, Category, Level, Duration_Hours, Max_Students, Is_Active) VALUES
('WD101', 'Introduction to Web Development', 'Fundamentals of HTML, CSS, and JavaScript for beginners.',       'Programming', 'Beginner',     40, 30, 1),
('JP201', 'Java Programming Essentials',     'Core Java concepts including OOP, collections, and exceptions.', 'Programming', 'Intermediate', 60, 25, 1),
('DB301', 'Database Management Systems',     'Relational databases, SQL, normalization, and transactions.',    'Database',    'Intermediate', 45, 30, 1),
('UX401', 'UI/UX Design Principles',         'User-centered design, wireframing, and prototyping techniques.', 'Design',      'Beginner',     35, 20, 1),
('MA501', 'Mobile App Development',          'Cross-platform mobile development using modern frameworks.',     'Programming', 'Advanced',     80, 20, 1);

-- ============================================================
-- COURSE_LESSONS
-- ============================================================
INSERT INTO Course_Lessons (Course_ID, Lesson_Title, Content, Lesson_Type, Duration_Minutes, Lesson_Order, Is_Preview) VALUES
(1, 'HTML Basics',        'Introduction to HTML tags and document structure.',    'Reading',  30, 1, 1),
(1, 'CSS Fundamentals',   'Styling with CSS: selectors, box model, and layouts.', 'Reading',  45, 2, 0),
(1, 'JavaScript Intro',   'Variables, functions, and DOM manipulation.',          'Video',    60, 3, 0),
(2, 'OOP in Java',        'Classes, objects, inheritance, and polymorphism.',     'Reading',  60, 1, 1),
(2, 'Java Collections',   'Lists, Maps, Sets, and iterators.',                    'Reading',  50, 2, 0),
(3, 'SQL Basics',         'SELECT, INSERT, UPDATE, DELETE statements.',           'Activity', 45, 1, 1),
(3, 'Normalization',      '1NF, 2NF, 3NF explained with practical examples.',     'Reading',  40, 2, 0),
(4, 'Design Thinking',    'Empathize, define, ideate, prototype, test.',          'Reading',  40, 1, 1),
(4, 'Wireframing 101',    'Low-fidelity and high-fidelity wireframes.',           'Activity', 50, 2, 0),
(5, 'Mobile UX Patterns', 'Common navigation and interaction patterns.',          'Reading',  45, 1, 1),
(5, 'Building Your First App', 'End-to-end walkthrough of a starter app.',        'Video',    90, 2, 0);

-- ============================================================
-- COURSE_ASSIGNMENTS
-- ============================================================
INSERT INTO Course_Assignments (Course_ID, Title, Instructions, Assignment_Type, Due_Date, Max_Score, Passing_Score, Is_Required) VALUES
(1, 'Build a Portfolio Page', 'Create a personal portfolio using HTML and CSS.',         'Project', '2026-06-01', 100, 60, 1),
(1, 'JS Calculator',          'Build a functional calculator using vanilla JavaScript.', 'Project', '2026-06-15', 100, 60, 1),
(1, 'HTML Quiz',              'Answer 20 questions about HTML fundamentals.',            'Quiz',    '2026-05-20',  50, 30, 1),
(2, 'Bank Account OOP',       'Implement a BankAccount class with deposit/withdrawal.',  'Project', '2026-06-10', 100, 60, 1),
(2, 'Collections Lab',        'Practice using ArrayList, HashMap and HashSet.',          'Lab',     '2026-06-05',  50, 30, 1),
(3, 'ER Diagram Exercise',    'Design an ER diagram for a library management system.',   'Project', '2026-06-20',  50, 30, 1),
(3, 'SQL Final Exam',         'Written exam covering all SQL and normalization topics.', 'Exam',    '2026-06-25', 100, 60, 1),
(4, 'Wireframe Submission',   'Submit a 5-screen wireframe of an app of your choice.',   'Project', '2026-06-12', 100, 60, 1),
(5, 'Final Mobile App',       'Build and demo a working cross-platform app.',            'Project', '2026-06-30', 100, 60, 1);

-- ============================================================
-- TEACHER_ASSIGNMENTS
-- All 10 Postgres teachers assigned across the 5 courses
-- with realistic Primary / Co-Instructor / TA roles
-- ============================================================
INSERT INTO Teacher_Assignments (Teacher_ID, Course_ID, Role, Assigned_Date, Is_Active) VALUES
-- Course 1: Web Development
('6',  1, 'Primary',            '2026-01-05', 1),
('7',  1, 'Co-Instructor',      '2026-01-06', 1),
('15', 1, 'Teaching Assistant', '2026-01-07', 1),

-- Course 2: Java Programming
('6',  2, 'Primary',            '2026-01-05', 1),
('8',  2, 'Co-Instructor',      '2026-01-06', 1),

-- Course 3: Database Management
('9',  3, 'Primary',            '2026-01-08', 1),
('10', 3, 'Teaching Assistant', '2026-01-09', 1),

-- Course 4: UI/UX Design
('11', 4, 'Primary',            '2026-01-10', 1),
('12', 4, 'Co-Instructor',      '2026-01-11', 1),

-- Course 5: Mobile App Development
('13', 5, 'Primary',            '2026-01-12', 1),
('14', 5, 'Co-Instructor',      '2026-01-13', 1);

-- ============================================================
-- ENROLLMENTS
-- 55 enrollment rows spread across the 40 students.
-- Many students take multiple courses. Status mix:
--   ~ 10% Completed (with grade + certificate)
--   ~ 75% Active (varied progress %)
--   ~ 15% Dropped
-- ============================================================
INSERT INTO Enrollments (Student_ID, Course_ID, Enrollment_Date, Completion_Date, Status,     Progress_Percent, Grade, Certificate_Issued) VALUES
-- ─── Course 1: Web Development (15 enrollments) ───
('16', 1, '2026-01-10', '2026-03-20', 'Completed', 100.00, 92.50, 1),
('17', 1, '2026-01-12', '2026-03-22', 'Completed', 100.00, 88.00, 1),
('18', 1, '2026-01-15', NULL,         'Active',     75.00, NULL,  0),
('19', 1, '2026-01-18', NULL,         'Active',     60.00, NULL,  0),
('20', 1, '2026-01-20', NULL,         'Active',     45.00, NULL,  0),
('21', 1, '2026-01-22', NULL,         'Dropped',    25.00, NULL,  0),
('22', 1, '2026-01-25', '2026-04-01', 'Completed', 100.00, 85.00, 1),
('23', 1, '2026-02-01', NULL,         'Active',     55.00, NULL,  0),
('24', 1, '2026-02-05', NULL,         'Active',     40.00, NULL,  0),
('25', 1, '2026-02-08', NULL,         'Active',     30.00, NULL,  0),
('26', 1, '2026-02-10', NULL,         'Dropped',    15.00, NULL,  0),
('30', 1, '2026-02-15', NULL,         'Active',     20.00, NULL,  0),
('35', 1, '2026-02-18', NULL,         'Active',     10.00, NULL,  0),
('40', 1, '2026-02-20', NULL,         'Active',      5.00, NULL,  0),
('45', 1, '2026-03-01', NULL,         'Active',     50.00, NULL,  0),

-- ─── Course 2: Java Programming (12 enrollments) ───
('16', 2, '2026-01-10', NULL,         'Active',     65.00, NULL,  0),
('19', 2, '2026-01-18', NULL,         'Active',     50.00, NULL,  0),
('22', 2, '2026-01-25', NULL,         'Active',     70.00, NULL,  0),
('27', 2, '2026-02-01', '2026-04-10', 'Completed', 100.00, 91.00, 1),
('28', 2, '2026-02-05', NULL,         'Active',     45.00, NULL,  0),
('29', 2, '2026-02-10', NULL,         'Dropped',    20.00, NULL,  0),
('31', 2, '2026-02-15', NULL,         'Active',     55.00, NULL,  0),
('32', 2, '2026-02-18', NULL,         'Active',     35.00, NULL,  0),
('33', 2, '2026-02-20', NULL,         'Active',     25.00, NULL,  0),
('34', 2, '2026-03-01', NULL,         'Active',     15.00, NULL,  0),
('41', 2, '2026-03-05', NULL,         'Active',     10.00, NULL,  0),
('46', 2, '2026-03-10', NULL,         'Active',      5.00, NULL,  0),

-- ─── Course 3: Database Management (10 enrollments) ───
('17', 3, '2026-01-12', NULL,         'Active',     80.00, NULL,  0),
('20', 3, '2026-01-20', NULL,         'Active',     50.00, NULL,  0),
('25', 3, '2026-02-08', NULL,         'Active',     65.00, NULL,  0),
('30', 3, '2026-02-15', NULL,         'Active',     40.00, NULL,  0),
('36', 3, '2026-02-22', '2026-04-15', 'Completed', 100.00, 89.50, 1),
('37', 3, '2026-02-25', NULL,         'Active',     55.00, NULL,  0),
('38', 3, '2026-03-01', NULL,         'Active',     30.00, NULL,  0),
('42', 3, '2026-03-05', NULL,         'Dropped',    18.00, NULL,  0),
('47', 3, '2026-03-10', NULL,         'Active',     22.00, NULL,  0),
('52', 3, '2026-03-15', NULL,         'Active',     12.00, NULL,  0),

-- ─── Course 4: UI/UX Design (10 enrollments) ───
('21', 4, '2026-01-22', NULL,         'Active',     70.00, NULL,  0),
('26', 4, '2026-02-10', NULL,         'Active',     45.00, NULL,  0),
('35', 4, '2026-02-18', NULL,         'Active',     60.00, NULL,  0),
('39', 4, '2026-02-22', '2026-04-20', 'Completed', 100.00, 94.00, 1),
('43', 4, '2026-03-05', NULL,         'Active',     35.00, NULL,  0),
('44', 4, '2026-03-08', NULL,         'Active',     25.00, NULL,  0),
('48', 4, '2026-03-12', NULL,         'Dropped',    10.00, NULL,  0),
('49', 4, '2026-03-15', NULL,         'Active',     50.00, NULL,  0),
('53', 4, '2026-03-18', NULL,         'Active',     15.00, NULL,  0),
('54', 4, '2026-03-20', NULL,         'Active',     20.00, NULL,  0),

-- ─── Course 5: Mobile App Development (8 enrollments) ───
('27', 5, '2026-02-01', NULL,         'Active',     35.00, NULL,  0),
('32', 5, '2026-02-18', NULL,         'Active',     45.00, NULL,  0),
('33', 5, '2026-03-18', NULL,         'Active',     20.00, NULL,  0),
('40', 5, '2026-02-25', NULL,         'Active',     55.00, NULL,  0),
('45', 5, '2026-03-01', '2026-04-25', 'Completed', 100.00, 87.00, 1),
('50', 5, '2026-03-08', NULL,         'Active',     25.00, NULL,  0),
('51', 5, '2026-03-12', NULL,         'Dropped',    15.00, NULL,  0),
('55', 5, '2026-03-15', NULL,         'Active',     30.00, NULL,  0);

-- ============================================================
-- COURSE_RATINGS
-- Verified ratings (Is_Verified = 1) from students who are/were
-- actively enrolled. Mix of scores 3–5 with realistic reviews.
-- ============================================================
INSERT INTO Course_Ratings (Student_ID, Course_ID, Rating_Score, Review_Text, Would_Recommend, Is_Verified) VALUES
-- ─── Course 1 ratings ───
('16', 1, 5, 'Excellent introduction to web development. Loved the hands-on approach!', 1, 1),
('17', 1, 5, 'Best course I have taken. Clear and well-paced.',                         1, 1),
('18', 1, 4, 'Really enjoying the lessons so far.',                                     1, 1),
('19', 1, 3, 'Pace is a bit slow for me but content is solid.',                         1, 1),
('22', 1, 4, 'Great content but could use more advanced topics.',                       1, 1),
('23', 1, 5, 'The CSS section was eye-opening.',                                        1, 1),

-- ─── Course 2 ratings ───
('16', 2, 4, 'Solid foundation for Java programming.',                                  1, 1),
('22', 2, 5, 'Loved the collections lab.',                                              1, 1),
('27', 2, 5, 'OOP concepts finally clicked thanks to this course.',                     1, 1),
('31', 2, 4, 'Helpful exercises throughout.',                                           1, 1),

-- ─── Course 3 ratings ───
('17', 3, 4, 'Good intro to SQL — assignments reinforce the material.',                 1, 1),
('20', 3, 3, 'A bit dense for beginners, but worth pushing through.',                   0, 1),
('25', 3, 4, 'Could use more real-world examples.',                                     1, 1),
('36', 3, 5, 'Normalization explained perfectly!',                                      1, 1),
('37', 3, 5, 'Engaging instructor.',                                                    1, 1),

-- ─── Course 4 ratings ───
('21', 4, 4, 'Wireframing section was the best part.',                                  1, 1),
('26', 4, 4, 'Solid course overall.',                                                   1, 1),
('35', 4, 5, 'Loved the design exercises.',                                             1, 1),
('39', 4, 5, 'Amazing UX principles course. Highly recommend!',                         1, 1),

-- ─── Course 5 ratings ───
('27', 5, 4, 'Challenging but rewarding.',                                              1, 1),
('32', 5, 3, 'Some sections felt rushed.',                                              1, 1),
('40', 5, 4, 'Good intro to mobile development.',                                       1, 1),
('45', 5, 5, 'Cross-platform mobile dev made easy.',                                    1, 1),
('55', 5, 5, 'Great practical examples.',                                               1, 1);

-- ============================================================
-- VERIFICATION
-- Uncomment to run these checks after seeding:
-- ============================================================
-- SELECT COUNT(*) AS total_enrollments FROM Enrollments;            -- expected: 55
-- SELECT COUNT(*) AS total_teacher_assignments FROM Teacher_Assignments;  -- expected: 11
-- SELECT COUNT(*) AS total_ratings FROM Course_Ratings;             -- expected: 24
--
-- -- Enrollment distribution by status
-- SELECT Status, COUNT(*) AS n FROM Enrollments GROUP BY Status;
--
-- -- Enrollment distribution by course
-- SELECT Course_ID, COUNT(*) AS n FROM Enrollments GROUP BY Course_ID ORDER BY Course_ID;
--
-- -- Average rating per course
-- SELECT Course_ID, AVG(Rating_Score) AS avg_score, COUNT(*) AS n
-- FROM Course_Ratings GROUP BY Course_ID ORDER BY Course_ID;
