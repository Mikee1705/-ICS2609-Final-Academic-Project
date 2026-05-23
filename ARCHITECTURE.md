# FAP — System Architecture

> Active Learning, Inc. Course Management System
> Reference for understanding how the backend layers connect and how data flows across **three** databases.

---

## 1. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                          BROWSER (Client)                            │
│         JSP pages • HTML forms • Download Report buttons             │
└─────────────────────────────┬────────────────────────────────────────┘
                              │ HTTP (GET / POST)
                              ▼
┌──────────────────────────────────────────────────────────────────────┐
│                       GLASSFISH SERVER                                │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  WEB LAYER  —  web/WEB-INF/web.xml                              │  │
│  │  • postgres.*  derby.*  mysql.* credentials                     │  │
│  │  • EncryptionKey + SecretKey (AES + reCAPTCHA)                  │  │
│  │  • report.header / report.footer                                │  │
│  │  • Error page mapping (401/403/404/500) → Error.jsp             │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                              │                                        │
│                              ▼                                        │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  SERVLET LAYER                                                  │  │
│  │  Servlets.*                                                     │  │
│  │   • LoginServlet, CaptchaServlet, LogoutServlet                 │  │
│  │   • CourseServlet, StudentServlet, InstructorServlet            │  │
│  │   • AnalyticsServlet, DevLoginServlet                           │  │
│  │  com.fap.report.*                                               │  │
│  │   • 5 PDF report servlets (Users/Courses/Enrollments/etc.)      │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                              │                                        │
│                              ▼                                        │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  DAO LAYER  —  com.fap.dao.*                                    │  │
│  │   • dao/derby/UserDAO       — auth                              │  │
│  │   • dao/postgres/StudentDAO, TeacherDAO, SalutationDAO          │  │
│  │   • dao/mysql/Course/Enrollment/TeacherAssignment/CourseRating  │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                              │                                        │
│                              ▼                                        │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  CONNECTION UTILITIES  —  com.fap.db.*                          │  │
│  │   • DerbyConnection      ← reads derby.*    from web.xml        │  │
│  │   • PostgresConnection   ← reads postgres.* from web.xml        │  │
│  │   • MySQLConnection      ← reads mysql.*    from web.xml        │  │
│  └────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬────────────────────────────────────────┘
                              │  JDBC
        ┌─────────────────────┼──────────────────────┐
        ▼                     ▼                      ▼
┌──────────────┐      ┌────────────────┐      ┌───────────────────┐
│    DERBY     │      │   POSTGRESQL   │      │      MYSQL         │
│  (LoginDB)   │      │   (postgres)   │      │ (fap_activelearning)│
│ Auth Layer   │      │ Profile Layer  │      │  Academic Layer    │
│              │      │                │      │                    │
│ • USERS      │◄────►│ • Students     │◄────►│ • Courses          │
│   USERNAME PK│      │ • Teachers     │      │ • Course_Lessons   │
│   PASSWORD   │      │ • Salutations  │      │ • Course_Assignments│
│   USERROLE   │      │ • Student_Phones│     │ • Enrollments      │
│              │      │                │      │ • Teacher_Assignments│
│              │      │ Username  ⇆    │      │ • Course_Ratings   │
│              │      │ Student/Teacher│      │   (Student/Teacher │
│              │      │   _ID          │      │    IDs are VARCHAR │
│              │      │                │      │    matching Postgres)│
└──────────────┘      └────────────────┘      └───────────────────┘
       ▲                       ▲                       ▲
       │                       │                       │
       └──── Identity ─────────┴──────── Foreign ──────┘
            bridge via              key (Postgres ID)
            Username                used by MySQL
```

---

## 2. The 3-DBMS Data Contract

| DBMS | Database | Owner | Purpose | Key Fields |
|---|---|---|---|---|
| **Derby** | `LoginDB` | Teammate 2 | Authentication only | `USERNAME` (PK), `PASSWORD` (AES), `USERROLE` |
| **PostgreSQL** | `postgres` | Teammate 3 | User profiles | `Student_ID` / `Teacher_ID` (VARCHAR PKs), `Username` (UNIQUE — bridge) |
| **MySQL** | `fap_activelearning` | Backend lead | Academic records | `Student_ID` / `Teacher_ID` (VARCHAR FKs to Postgres) |

### ID Convention

| ID Range | Role | DB Location |
|---|---|---|
| `1–5` | Admins | Derby only |
| `6–15` | Teachers | Derby (auth) + Postgres (profile) |
| `16–55` | Students | Derby (auth) + Postgres (profile) |

### Identity Bridge

```
Derby.USERS.USERNAME  ⇆  Postgres.Students.Username (UNIQUE)
                      ⇆  Postgres.Teachers.Username (UNIQUE)

Then:  Postgres.Students.Student_ID  →  MySQL.Enrollments.Student_ID
       Postgres.Students.Student_ID  →  MySQL.Course_Ratings.Student_ID
       Postgres.Teachers.Teacher_ID  →  MySQL.Teacher_Assignments.Teacher_ID
```

This means: given a logged-in Derby username, we can JOIN through Postgres to find the matching MySQL records. That's how `scope=mine` PDF reports work.

---

## 3. Folder Structure

```
FAP/
├── README.md                        ← Setup guide for teammates
├── ARCHITECTURE.md                  ← This document
│
├── sql/
│   ├── mysql_setup.sql              ← Run for MySQL: 6 tables + seed
│   ├── derby_setup.sql              ← Run for Derby: USERS table + 55 users
│   ├── postgres_setup.sql           ← Run for Postgres (teammate 3's file)
│   └── mysql_migration_varchar_ids.sql  ← One-time migration if MySQL was set up earlier
│
├── src/java/com/fap/
│   ├── db/                          ← Connection utilities
│   │   ├── DerbyConnection.java
│   │   ├── MySQLConnection.java
│   │   └── PostgresConnection.java
│   │
│   ├── model/                       ← Plain POJOs
│   │   ├── User.java                ← Derby
│   │   ├── Student.java             ← Postgres
│   │   ├── Teacher.java             ← Postgres
│   │   ├── Salutation.java          ← Postgres
│   │   ├── StudentPhone.java        ← Postgres
│   │   ├── Course.java              ← MySQL
│   │   ├── CourseAssignment.java    ← MySQL
│   │   ├── CourseLesson.java        ← MySQL
│   │   ├── Enrollment.java          ← MySQL (Student_ID is String)
│   │   ├── TeacherAssignment.java   ← MySQL (Teacher_ID is String)
│   │   └── CourseRating.java        ← MySQL (Student_ID is String)
│   │
│   ├── dao/
│   │   ├── derby/
│   │   │   └── UserDAO.java
│   │   ├── postgres/
│   │   │   ├── StudentDAO.java      ← incl. getStudentByUsername (identity bridge)
│   │   │   ├── TeacherDAO.java      ← incl. getTeacherByUsername (identity bridge)
│   │   │   └── SalutationDAO.java
│   │   └── mysql/
│   │       ├── CourseDAO.java
│   │       ├── EnrollmentDAO.java
│   │       ├── TeacherAssignmentDAO.java
│   │       └── CourseRatingDAO.java
│   │
│   ├── report/                      ← iText 5 PDF generation
│   │   ├── PdfReportBuilder.java
│   │   ├── PageNumberEventHandler.java
│   │   ├── UserListReportServlet.java
│   │   ├── CourseListReportServlet.java
│   │   ├── EnrollmentReportServlet.java         ← scope=mine via Postgres bridge
│   │   ├── TeacherAssignmentReportServlet.java  ← scope=mine via Postgres bridge
│   │   └── CourseRatingReportServlet.java       ← scope=mine via Postgres bridge
│   │
│   └── util/
│       ├── DateRangeParser.java
│       └── DerbySeed.java
│
├── src/java/Servlets/               ← Controller layer
│   ├── LoginServlet.java
│   ├── CaptchaServlet.java
│   ├── LogoutServlet.java
│   ├── Security.java                ← AES encrypt/decrypt
│   ├── AuthFilter.java              ← session+role checks
│   ├── CourseServlet.java
│   ├── StudentServlet.java          ← Derby + Postgres cross-DB writes
│   ├── InstructorServlet.java       ← Derby + Postgres cross-DB writes
│   ├── AnalyticsServlet.java
│   ├── DevLoginServlet.java         ← DEV ONLY — remove before submit
│   ├── User.java                    ← (legacy POJO, kept for LoginServlet)
│   └── ...
│
└── web/
    ├── index.jsp                    ← login form
    ├── Header.jsp                   ← shared nav (loads Bootstrap once)
    ├── Footer.jsp
    ├── AdminAnalytics.jsp           ← chart dashboard
    ├── Courses_CRUD.jsp
    ├── Students_CRUD.jsp            ← Postgres profile fields
    ├── Instructors_CRUD.jsp         ← Postgres profile fields
    ├── Error.jsp / LoginError.jsp
    └── WEB-INF/
        ├── web.xml                  ← All 3 DB credentials + secrets
        └── lib/                     ← derbyclient + mysql + postgres + itextpdf
```

---

## 4. Key Data Flows

### 🔵 Admin loads the Students CRUD page

```
GET /StudentServlet
  │
  ▼
StudentServlet.doGet():
  ├─ AuthFilter.requireAdmin() ✓
  ├─ StudentDAO.getAllStudents()      → PostgreSQL SELECT JOIN Salutations
  ├─ SalutationDAO.getAllSalutations() → PostgreSQL SELECT
  ├─ req.setAttribute("students", ...)
  └─ forward → Students_CRUD.jsp
              renders Student_ID | Username | Full Name | Email | Funding | Registered
```

### 🟢 Admin adds a new Student (cross-DB write)

```
POST /StudentServlet?action=add
  body: StudentId, Username, FirstName, LastName, Email, Funding, SalutationId, Password
  │
  ▼
StudentServlet.handleAdd():
  ├─ UserDAO.insertUser(username, role=Student, password)  → DERBY
  │     • password AES-encrypted via Security.Encrypt
  ├─ StudentDAO.insertStudent(...)                          → POSTGRES
  │     • Student_ID, Username, salutation, name, email, funding
  └─ redirect → /StudentServlet  (PRG pattern)
```

### 🟠 Student downloads "My Enrollments" PDF

```
GET /report/enrollments?scope=mine
  │
  ▼
EnrollmentReportServlet:
  ├─ session.UName  = e.g. "sdomingo"
  ├─ session.Role   = "Student"
  ├─ StudentDAO.getStudentByUsername("sdomingo")  → POSTGRES
  │     returns Student{ student_id="16", ... }
  ├─ EnrollmentDAO.getEnrollmentsByStudent("16")  → MYSQL
  │     returns List<Enrollment> for that student
  └─ PdfReportBuilder streams ENROLLMENTLIST_<timestamp>.pdf
```

---

## 5. Encryption Model

Single source of truth: **`Servlets.Security`** (AES/ECB/PKCS5Padding) with the key in `web.xml`.

| Where used | Operation |
|---|---|
| `LoginServlet.getRecords()` | Decrypt — compares plaintext to typed password |
| `UserDAO.insertUser()` | Encrypt — before INSERT |
| `UserDAO.updatePassword()` | Encrypt — before UPDATE |
| `UserDAO.mapUser()` | Decrypt — when reading rows |
| `DerbySeed.main()` | Encrypt — for generating seed INSERTs |

No SHA-256, no parallel crypto — exactly one library.

---

## 6. Session Contract

| Attribute | Type | Set by | Used by |
|---|---|---|---|
| `UName` | String | `LoginServlet` / `DevLoginServlet` | All authenticated JSPs + report servlets |
| `Role` | String | `LoginServlet` / `DevLoginServlet` | `AuthFilter`, JSP auth guards |
| `Title` / `Error` | String | `LoginServlet`, `CaptchaServlet` | `LoginError.jsp` |

PDF report servlets reconstruct a `User` object from these on each call. The session never stores a heavy object.

---

## 7. URL Map

| URL | Method | Auth | Purpose |
|---|---|---|---|
| `/` → `index.jsp` | GET | none | Login form |
| `/CaptchaServlet` | POST | none | Verifies reCAPTCHA → forwards to /Login |
| `/Login` | POST | none | Authenticates → sets session → redirect /Analytics |
| `/Logout` | GET | logged-in | Invalidates session → /index.jsp |
| `/DevLogin` | GET | none | **DEV ONLY** — bypass auth, become admin |
| `/Analytics` | GET | Admin | Dashboard charts (real MySQL data) |
| `/CourseServlet` | GET/POST | Admin | Courses CRUD (MySQL) |
| `/StudentServlet` | GET/POST | Admin | Students CRUD (Derby + Postgres) |
| `/InstructorServlet` | GET/POST | Admin | Instructors CRUD (Derby + Postgres) |
| `/report/users` | GET | Admin | Derby USERS PDF |
| `/report/courses` | GET | logged-in | MySQL Courses PDF |
| `/report/enrollments` | GET | Admin / Student | MySQL Enrollments PDF |
| `/report/teacher-assignments` | GET | Admin / Teacher | MySQL Teacher Assignments PDF |
| `/report/ratings` | GET | Admin / Student | MySQL Course Ratings PDF |

**Query params** on all `/report/*`:
- `scope=all` → admin sees everything
- `scope=mine` → uses Postgres identity bridge to filter to logged-in user
- `from=YYYY-MM-DD&to=YYYY-MM-DD` → time-bound

---

## 8. Spec Coverage (FAP rubric)

| Section | Status |
|---|---|
| Reports (25pt) — landscape PDF, paginated, headers/footers, `*` for current admin, time-bound, all/mine | 🟢 Done |
| Multiple DBMS (25pt) — Derby + MySQL + PostgreSQL all wired into servlets and JSPs | 🟢 Done |
| Security: AES password encryption + reCAPTCHA | 🟢 Done |
| UI/UX responsive Bootstrap | 🟢 Done |
| Context: clear Auth / Profile / Academic separation | 🟢 Done |
| Usability: custom Error.jsp, try/catch on DAOs, prepared statements | 🟢 Done |
| Presentation (video) | ⚪ TODO |

---

## 9. Before Final Submission

- [ ] Delete `Servlets.DevLoginServlet` (or rename to make obvious it should be removed)
- [ ] Run `sql/mysql_migration_varchar_ids.sql` on any pre-existing MySQL data
- [ ] Verify all 55 Derby users have an AES-encrypted password (rerun `DerbySeed.main()`)
- [ ] Confirm `web.xml` postgres.password matches the local install
- [ ] Record the demo video
