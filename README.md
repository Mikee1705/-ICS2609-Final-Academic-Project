# FAP — Final Academic Project

> **Active Learning, Inc. Course Management System**
> ICS2609 | Java EE web application with three-DBMS architecture

A web-based course management system built on Java EE (Servlets + JSP) running on GlassFish, with data split across **three** relational databases:

| DBMS | Role | Database name |
|---|---|---|
| **Apache Derby** | Authentication (`USERS` table) | `LoginDB` |
| **PostgreSQL 18** | User profiles (Students, Teachers, Salutations, Phones) | `postgres` |
| **MySQL 8** | Academic data (Courses, Enrollments, Ratings, etc.) | `fap_activelearning` |

For full architecture details, read [`ARCHITECTURE.md`](ARCHITECTURE.md).

---

## ⚡ Quick Start

1. Clone the repo and open it in **NetBeans 12.4**
2. Start three databases:
   - **MySQL** via XAMPP Control Panel
   - **Derby** via NetBeans Services → Databases → Java DB → Start Server
   - **PostgreSQL** via Windows Services (`postgresql-x64-18`)
3. Run all three setup scripts (details below)
4. Right-click project → **Clean and Build** → **Run** (F6)
5. Browser opens at `http://localhost:8080/FAP`
6. To skip the login form during dev, hit `http://localhost:8080/FAP/DevLogin`

---

## 📦 Prerequisites

| Tool | Version | Why |
|---|---|---|
| **NetBeans IDE** | 12.4 | Project format & GlassFish server |
| **JDK** | 8 | Compile target |
| **GlassFish** | 4.x (bundled w/ NetBeans 12.4) | Servlet container |
| **XAMPP** | 8.x | Provides MySQL 8 |
| **PostgreSQL** | 18 | User profile data |
| **Java DB (Derby)** | bundled w/ NetBeans | Authentication |

---

## 🗄️ Database Setup

All credentials are read from `web/WEB-INF/web.xml`. Don't rename databases.

### 1️⃣ MySQL (XAMPP)

1. Open **XAMPP Control Panel** → start **MySQL** (port 3306)
2. In NetBeans → **Services** tab → register the driver:
   - Right-click **Drivers** → **New Driver**
   - Add: `web/WEB-INF/lib/mysql-connector-j-9.6.0.jar`
   - Driver class: `com.mysql.cj.jdbc.Driver`
3. Right-click **Databases** → **New Connection**:
   - Host: `localhost`  Port: `3306`
   - User: `root`  Password: *(blank — XAMPP default)*
4. Run this once to create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS fap_activelearning;
   ```
5. Reconnect with `Database: fap_activelearning` and run, in order:
   - `sql/mysql_setup.sql` (creates the 6 tables)
   - `sql/mysql_seeder.sql` (wipes + reseeds with realistic demo data)

### 2️⃣ Derby (Java DB)

1. **Services** → expand **Databases** → **Java DB** → right-click → **Start Server**
2. Right-click **Databases** → **New Connection**:
   - JDBC URL: `jdbc:derby://localhost:1527/LoginDB;create=true`
   - User: `APP`  Password: `APP`
   - Schema: `APP`
3. Right-click the connection → **Execute Command**
4. Paste the contents of `sql/derby_setup.sql` → run
   *(self-contained — creates the `USERS` table AND seeds all 55 demo users with AES-encrypted passwords)*

### 3️⃣ PostgreSQL

1. Install **PostgreSQL 18** (Windows installer) — set a password during install
2. Add the JDBC driver in NetBeans:
   - Right-click **Drivers** → **New Driver**
   - Add: `web/WEB-INF/lib/postgresql-42.7.11.jar`
   - Driver class: `org.postgresql.Driver`
3. Create connection:
   - JDBC URL: `jdbc:postgresql://localhost:5432/postgres`
   - User: `postgres`  Password: *(what you set during install)*
   - Schema: `public`
4. Right-click the connection → **Execute Command**
5. Paste the contents of `sql/postgres_setup.sql` → run
   *(self-contained — creates `Salutations`, `Students`, `Teachers`, `Student_Phones` AND seeds 10 teachers + 40 students with the Username identity bridge)*

### 4️⃣ Update web.xml passwords

Open `web/WEB-INF/web.xml` and set the PostgreSQL password to match what you used during install:

```xml
<context-param>
    <param-name>postgres.password</param-name>
    <param-value>YOUR_PG_PASSWORD_HERE</param-value>
</context-param>
```

---

## 🚀 Running the App

1. In NetBeans, right-click the **FAP** project → **Clean and Build**
2. Right-click → **Run** (F6)
3. GlassFish starts and your browser opens at the login page

### Two login paths

| Path | Use when |
|---|---|
| `http://localhost:8080/FAP/` | Real flow: captcha + Derby auth |
| `http://localhost:8080/FAP/DevLogin` | Dev shortcut: instant admin session (skips captcha) |

Default admin credentials (after running `DerbySeed`):

| Username | Password |
|---|---|
| `admin` | `Password123` |

⚠️ Delete `DevLoginServlet.java` before final submission.

---

## 🌐 URL Map

| URL | Method | Purpose | Role |
|---|---|---|---|
| `/` | GET | Login page | none |
| `/CaptchaServlet` | POST | Verifies reCAPTCHA → forwards to `/Login` | none |
| `/Login` | POST | Authenticates against Derby → sets session | none |
| `/Logout` | GET | Invalidates session, redirects to login | logged-in |
| `/DevLogin` | GET | **DEV ONLY** — instant admin login | none |
| `/Analytics` | GET | Dashboard with Chart.js graphs | Admin |
| `/CourseServlet` | GET | List all courses (MySQL) | Admin |
| `/CourseServlet?action=add` | POST | Add a course | Admin |
| `/CourseServlet?action=edit` | POST | Edit a course | Admin |
| `/CourseServlet?action=delete` | POST | Delete a course | Admin |
| `/StudentServlet` | GET | List students (Postgres profile + Derby auth) | Admin |
| `/StudentServlet?action=add` | POST | Add to BOTH Derby + Postgres | Admin |
| `/StudentServlet?action=delete` | POST | Delete from BOTH | Admin |
| `/InstructorServlet` | GET/POST | Same pattern as students | Admin |
| `/report/users` | GET | User list PDF (Derby) | Admin |
| `/report/courses` | GET | Course catalog PDF | logged-in |
| `/report/enrollments?scope=all\|mine` | GET | Enrollments PDF | Admin/Student |
| `/report/teacher-assignments?scope=...` | GET | Teacher assignments PDF | Admin/Teacher |
| `/report/ratings?scope=...` | GET | Course ratings PDF | Admin/Student |

All `/report/*` endpoints accept `?from=YYYY-MM-DD&to=YYYY-MM-DD` for time-bound filtering.

---

## 🗃️ Tables Reference

### Derby (`LoginDB`)

| Table | Columns | Purpose |
|---|---|---|
| **USERS** | `USERNAME` PK, `PASSWORD` (AES), `USERROLE` | Authentication for all 3 roles |

### PostgreSQL (`postgres.public`)

| Table | Key Columns | Purpose |
|---|---|---|
| **Salutations** | `Salutation_ID` PK, `Title` | Lookup table (Mr./Ms./Mrs./Dr./Prof.) |
| **Students** | `Student_ID` PK, `Username` UNIQUE, `First_Name`, `Last_Name`, `Email`, `Funding`, `Registration_Date`, `Salutation_ID` FK | Student profile data |
| **Teachers** | `Teacher_ID` PK, `Username` UNIQUE, `First_Name`, `Last_Name`, `Salutation_ID` FK | Teacher profile data |
| **Student_Phones** | `Phone_ID` PK, `Student_ID` FK, `Phone_Number`, `Phone_Type` | One-to-many student phones |

**Identity bridge:** `Username` in Postgres `Students`/`Teachers` matches `USERNAME` in Derby `USERS`, so the system can JOIN across DBs.

### MySQL (`fap_activelearning`)

| Table | Key Columns | Purpose |
|---|---|---|
| **Courses** | `Course_ID` PK, `Course_Code` UNIQUE, `Course_Name`, `Description`, `Category`, `Level`, `Duration_Hours`, `Max_Students`, `Is_Active` | Course catalog |
| **Course_Lessons** | `Lesson_ID` PK, `Course_ID` FK, `Lesson_Title`, `Content`, `Lesson_Type`, `Duration_Minutes`, `Lesson_Order`, `Is_Preview` | Lessons within a course |
| **Course_Assignments** | `Assignment_ID` PK, `Course_ID` FK, `Title`, `Instructions`, `Assignment_Type`, `Due_Date`, `Max_Score`, `Passing_Score`, `Is_Required` | Assignments per course |
| **Enrollments** | `Enrollment_ID` PK, `Student_ID` (VARCHAR), `Course_ID` FK, `Enrollment_Date`, `Status`, `Progress_Percent`, `Grade`, `Certificate_Issued`, `Completion_Date`, `Last_Accessed` | Student ↔ course junction (3NF) |
| **Teacher_Assignments** | `Assignment_ID` PK, `Teacher_ID` (VARCHAR), `Course_ID` FK, `Role`, `Assigned_Date`, `End_Date`, `Is_Active` | Teacher ↔ course junction (3NF) |
| **Course_Ratings** | `Rating_ID` PK, `Student_ID` (VARCHAR), `Course_ID` FK, `Rating_Score` (1–5), `Review_Text`, `Would_Recommend`, `Is_Verified`, `Rated_At` | 1–5 ratings + reviews |

**Note:** `Student_ID` and `Teacher_ID` in MySQL are VARCHAR(50) on purpose — they match the Postgres primary keys (which are also VARCHAR).

---

## 🧠 DAO Methods Reference

### `com.fap.dao.derby.UserDAO`

| Method | Purpose |
|---|---|
| `getAllUsers()` | All users ordered by role |
| `getUsersByRole(role)` | Filter by `Admin`/`Teacher`/`Student` |
| `getUserByUsername(username)` | Lookup for login |
| `authenticate(username, plainPassword)` | Validates credentials, returns User or null |
| `insertUser(user, plainPassword)` | Hashes password via Security, then inserts |
| `updatePassword(username, newPlain)` | Re-encrypts and updates |
| `updateRole(username, newRole)` | Promote/demote |
| `deleteUser(username)` | Remove user |

### `com.fap.dao.postgres.StudentDAO`

| Method | Purpose |
|---|---|
| `getAllStudents()` | All students with salutation joined |
| `getStudentById(studentId)` | Lookup by Student_ID |
| `getStudentByUsername(username)` | **Identity bridge** — Derby username → Postgres profile |
| `getPhonesForStudent(studentId)` | All phone numbers for a student |
| `insertStudent(student)` | Create profile |
| `updateStudent(student)` | Update profile |
| `deleteStudent(studentId)` | Delete (cascades to phones) |
| `insertPhone(phone)` | Add a phone number |

### `com.fap.dao.postgres.TeacherDAO`

| Method | Purpose |
|---|---|
| `getAllTeachers()` | All teachers with salutation joined |
| `getTeacherById(teacherId)` | Lookup by Teacher_ID |
| `getTeacherByUsername(username)` | **Identity bridge** — Derby username → Postgres profile |
| `insertTeacher(teacher)` | Create profile |
| `updateTeacher(teacher)` | Update profile |
| `deleteTeacher(teacherId)` | Delete |

### `com.fap.dao.postgres.SalutationDAO`

| Method | Purpose |
|---|---|
| `getAllSalutations()` | Lookup table contents for dropdowns |

### `com.fap.dao.mysql.CourseDAO`

| Method | Purpose |
|---|---|
| `getAllCourses()` | All courses |
| `getCourseById(courseId)` | Lookup |
| `insertCourse(course)` | Create |
| `updateCourse(course)` | Update |
| `deactivateCourse(courseId)` | Soft delete (Is_Active = 0) |
| `deleteCourse(courseId)` | Hard delete (cascades to children) |
| `getAssignmentsByCourse(courseId)` | All assignments for a course |
| `insertAssignment(assignment)` | Add assignment |
| `getLessonsByCourse(courseId)` | All lessons for a course |
| `insertLesson(lesson)` | Add lesson |
| `getCourseReportByDateRange(from, to)` | Time-bound report |

### `com.fap.dao.mysql.EnrollmentDAO`

| Method | Purpose |
|---|---|
| `getAllEnrollments()` | All enrollments with course name |
| `getEnrollmentsByStudent(studentId)` | Per-student enrollments |
| `getEnrollmentsByCourse(courseId)` | Per-course enrollments |
| `insertEnrollment(enrollment)` | Create |
| `updateStatus(enrollmentId, status)` | Active/Completed/Dropped |
| `updateProgress(enrollmentId, percent)` | Update progress + last accessed |
| `deleteEnrollment(enrollmentId)` | Remove |
| `getAllEnrollmentsByDateRange(from, to)` | Time-bound report |
| `getEnrollmentsByStudentAndDateRange(studentId, from, to)` | Per-student time-bound |
| `getEnrollmentStatusSummary()` | Counts grouped by status (for charts) |

### `com.fap.dao.mysql.TeacherAssignmentDAO`

| Method | Purpose |
|---|---|
| `getAllTeacherAssignments()` | All assignments |
| `getAssignmentsByTeacher(teacherId)` | Per-teacher |
| `insertTeacherAssignment(ta)` | Create |
| `deactivateAssignment(id)` | Soft end (Is_Active = 0) |
| `deleteTeacherAssignment(id)` | Hard delete |
| `getAssignmentsByDateRange(from, to)` | Time-bound |
| `getAssignmentsByTeacherAndDateRange(teacherId, from, to)` | Per-teacher time-bound |

### `com.fap.dao.mysql.CourseRatingDAO`

| Method | Purpose |
|---|---|
| `getAllRatings()` | All ratings |
| `getRatingsByStudent(studentId)` | Per-student |
| `getRatingsByCourse(courseId)` | Per-course |
| `getAverageRating(courseId)` | Average score |
| `insertRating(rating)` | Submit (validates 1–5) |
| `updateRating(id, score, reviewText)` | Edit existing |
| `deleteRating(id)` | Remove |
| `getRatingsByDateRange(from, to)` | Time-bound |
| `getAverageRatingsPerCourse()` | Per-course aggregates (for charts) |

---

## 🛠️ Utility Classes

| Class | Purpose | How to run |
|---|---|---|
| `com.fap.util.DerbySeed` | Generates AES-encrypted INSERT statements (used to bootstrap `derby_setup.sql`; only needed if you want to add new users with a different default password) | Right-click → **Run File**, copy console output |
| `com.fap.util.DerbyReencrypt` | Re-encrypts existing `USERS.PASSWORD` rows when the encryption key changes between machines | Edit `OLD_KEY` and `NEW_KEY`, then right-click → **Run File** |
| `com.fap.util.DateRangeParser` | Parses `?from=...&to=...` request params with sensible fallbacks | Used internally by report servlets |
| `Servlets.Security` | AES/ECB/PKCS5Padding encrypt + decrypt (single source of truth for password crypto) | Called by `LoginServlet`, `UserDAO`, `DerbySeed` |
| `Servlets.AuthFilter` | Helper for session/role checks in every controller | `if (!AuthFilter.requireAdmin(req, resp)) return;` |

---

## 📄 SQL Scripts

See `sql/README.md` for a focused run-order guide. Quick reference:

| File | Purpose | When to run |
|---|---|---|
| `sql/derby_setup.sql` | **Complete Derby setup** — `USERS` table + 55 pre-encrypted seed users | Once, on first setup |
| `sql/postgres_setup.sql` | **Complete Postgres setup** — schema + Salutations + 10 Teachers + 40 Students (with Username bridge) | Once, on first setup |
| `sql/mysql_setup.sql` | Creates the 6 MySQL tables (schema only) | Once, before `mysql_seeder.sql` |
| `sql/mysql_seeder.sql` | Full reset + realistic seed data (55 enrollments, 11 teacher assignments, 24 ratings) | Anytime you want fresh demo data |
| `sql/mysql_migration_varchar_ids.sql` | Legacy ALTER for INT → VARCHAR IDs | Only if MySQL was set up before the Postgres integration |

---

## 🔐 Security

| Concern | Implementation |
|---|---|
| **SQL injection** | All DAOs use `PreparedStatement` with `?` placeholders |
| **Password storage** | AES/ECB/PKCS5Padding via `Servlets.Security` (key in `web.xml`) |
| **Sensitive config** | DB credentials, encryption keys, reCAPTCHA secret all in `web/WEB-INF/web.xml` |
| **Session auth** | `LoginServlet` sets `UName` + `Role` on the session; `AuthFilter` validates per request |
| **Role gates** | Admin-only endpoints return HTTP 403 to non-admins |
| **Captcha** | Google reCAPTCHA verified server-side in `CaptchaServlet` |

---

## 🩺 Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `Communications link failure` (MySQL) | MySQL not running | Start MySQL in XAMPP |
| `Userid or password invalid` (Derby) | Derby credentials don't match `web.xml` | Verify in Services → Properties, sync `web.xml` |
| `Connection refused` (Postgres) | Postgres service stopped | Start `postgresql-x64-18` in Windows services |
| Login fails with "DB Connection Failed" | One of the above | Check console output for the actual JDBC error |
| Login fails silently for valid user | Existing `USERS` rows encrypted with a different key | Run `DerbyReencrypt` (Option 3 in chat history) or wipe + reseed |
| Empty student/teacher list on CRUD pages | Postgres `Students`/`Teachers` empty | Run the Postgres setup script |
| `Foreign key constraint fails` on MySQL seed | Courses table empty | Run `sql/mysql_seeder.sql` (self-contained — wipes everything then reseeds) |
| Bootstrap dropdown looks broken | Browser cached old CSS | Hard refresh (`Ctrl + Shift + R`) |
| `Tracking Prevention blocked storage` warning | Edge's privacy feature (harmless) | Ignore or switch to Chrome/Firefox |

## 📚 Further Reading

- [`ARCHITECTURE.md`](ARCHITECTURE.md) — full system architecture diagram, data flows, design decisions
