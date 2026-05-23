# FAP — System Architecture

> Active Learning, Inc. Course Management System
> A reference for understanding how the backend layers connect and how data flows through the app.

---

## 1. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          BROWSER (Client)                            │
│         JSP pages, HTML forms, "Download Report" buttons             │
└─────────────────────────────┬───────────────────────────────────────┘
                              │ HTTP (GET / POST)
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      GLASSFISH SERVER (Tomcat-EE)                    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  WEB LAYER  —  /web/WEB-INF/web.xml                          │    │
│  │   • Reads credentials, report headers/footers                │    │
│  │   • Maps error pages (403, 404, 500)                         │    │
│  │   • Session config (30 min timeout)                          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  SERVLET LAYER  —  /src/java/com/fap/report/*                │    │
│  │   • UserListReportServlet                                    │    │
│  │   • CourseListReportServlet                                  │    │
│  │   • EnrollmentReportServlet                                  │    │
│  │   • TeacherAssignmentReportServlet                           │    │
│  │   • CourseRatingReportServlet                                │    │
│  │   (Plus future: LoginServlet, AdminServlet, etc.)            │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  DAO LAYER  —  /src/java/com/fap/dao/                        │    │
│  │   • dao/derby/UserDAO       (Derby)                          │    │
│  │   • dao/mysql/CourseDAO     (MySQL)                          │    │
│  │   • dao/mysql/EnrollmentDAO (MySQL)                          │    │
│  │   • dao/mysql/TeacherAssignmentDAO (MySQL)                   │    │
│  │   • dao/mysql/CourseRatingDAO (MySQL)                        │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  CONNECTION UTILITIES  —  /src/java/com/fap/db/              │    │
│  │   • MySQLConnection    reads mysql.* params from web.xml     │    │
│  │   • DerbyConnection    reads derby.* params from web.xml     │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────┬───────────────────────────────────────┘
                              │  JDBC
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│    DERBY     │      │    MYSQL     │      │  (DBMS #3)   │
│ (Auth Layer) │      │ (Academic)   │      │  Pending     │
│              │      │              │      │              │
│  • USERS     │      │  • Courses   │      │  TBD         │
│              │      │  • Lessons   │      │              │
│              │      │  • Assigns   │      │              │
│              │      │  • Enrollment│      │              │
│              │      │  • Teacher_Ax│      │              │
│              │      │  • Ratings   │      │              │
└──────────────┘      └──────────────┘      └──────────────┘
```

---

## 2. Folder Structure

```
FAP/
├── README.md                                ← Setup guide for teammates
├── ARCHITECTURE.md                          ← This document
├── build.xml                                ← NetBeans Ant build script
│
├── nbproject/                               ← NetBeans config (relative paths)
│   ├── project.properties                   ← Library references → web/WEB-INF/lib
│   └── private/                             ← Per-machine (gitignored)
│
├── sql/
│   ├── mysql_setup.sql                      ← Run to create MySQL tables + seed
│   └── derby_setup.sql                      ← Run to create Derby USERS + 55 seed users
│
├── src/java/com/fap/
│   ├── db/
│   │   ├── MySQLConnection.java             ← MySQL JDBC, reads web.xml params
│   │   └── DerbyConnection.java             ← Derby JDBC, reads web.xml params
│   │
│   ├── model/                               ← Plain POJOs (one per DB table)
│   │   ├── User.java                        ← Derby
│   │   ├── Course.java                      ← MySQL
│   │   ├── CourseAssignment.java
│   │   ├── CourseLesson.java
│   │   ├── Enrollment.java
│   │   ├── TeacherAssignment.java
│   │   └── CourseRating.java
│   │
│   ├── dao/
│   │   ├── derby/
│   │   │   └── UserDAO.java                 ← getAllUsers, authenticate, insertUser
│   │   └── mysql/
│   │       ├── CourseDAO.java
│   │       ├── EnrollmentDAO.java
│   │       ├── TeacherAssignmentDAO.java
│   │       └── CourseRatingDAO.java
│   │
│   ├── report/                              ← iText 5 PDF generation
│   │   ├── PdfReportBuilder.java            ← Shared: landscape, filename, response headers
│   │   ├── PageNumberEventHandler.java      ← Header/footer + Page X of Y on every page
│   │   ├── UserListReportServlet.java       ← /report/users
│   │   ├── CourseListReportServlet.java     ← /report/courses
│   │   ├── EnrollmentReportServlet.java     ← /report/enrollments
│   │   ├── TeacherAssignmentReportServlet.java ← /report/teacher-assignments
│   │   └── CourseRatingReportServlet.java   ← /report/ratings
│   │
│   ├── dao/postgres/                        ← Placeholder for the 3rd DBMS (PostgreSQL 18)
│   │   └── README.md                        ← Instructions for the teammate working on Postgres
│   │
│   └── util/
│       ├── DerbySeed.java                   ← main() generates AES-encrypted INSERT statements
│       └── DateRangeParser.java             ← Parses ?from=YYYY-MM-DD&to=YYYY-MM-DD
│
└── web/
    ├── index.html                           ← Frontend placeholder (login screen TBD)
    ├── Scripts/modal.js
    ├── Styles/styles.css, Modal.css
    └── WEB-INF/
        ├── web.xml                          ← Deployment descriptor (DB creds, headers, error pages)
        ├── glassfish-web.xml                ← GlassFish-specific config
        └── lib/                             ← All JARs committed here
            ├── derbyclient.jar
            ├── itextpdf-5.5.13.5.jar
            └── mysql-connector-j-9.6.0.jar
```

---

## 3. Layer Responsibilities

### **Web Layer** (`web.xml`)
The single source of truth for **sensitive config**. As required by the spec, no credentials are hard-coded in Java.

| Param | Purpose |
|---|---|
| `mysql.driver`, `mysql.url`, `mysql.username`, `mysql.password` | MySQL connection |
| `derby.driver`, `derby.url`, `derby.username`, `derby.password` | Derby connection |
| `report.header`, `report.footer` | Used by every PDF report |
| `app.name`, `app.version` | App metadata |

### **Servlet Layer** (`com.fap.report.*`)
Each servlet:
1. **Checks the session** — pulls the logged-in `User` from `session.getAttribute("user")`
2. **Authorizes the action** — `scope=all` requires Admin role
3. **Parses params** — `?scope=...`, `?from=...`, `?to=...`
4. **Calls the DAO** — fetches the data
5. **Hands off to `PdfReportBuilder`** — builds & streams the PDF

### **DAO Layer** (`com.fap.dao.*`)
- Uses **PreparedStatements** everywhere → SQL injection safe
- Maps each `ResultSet` row to a model POJO via private `map*()` methods
- Includes **report-specific queries** like `getAllEnrollmentsByDateRange()`

### **DB Layer** (`com.fap.db.*`)
- Reads connection params from `ServletContext` (which reads `web.xml`)
- Returns a fresh `Connection` per call → DAO closes it via try-with-resources

---

## 4. Data Flow Examples

### 🔵 Example A — Admin downloads "All Users" PDF

```
┌────────┐
│Browser │  click "Download User List"
└───┬────┘
    │ GET /report/users
    ▼
┌────────────────────────────────────────┐
│ UserListReportServlet.doGet()          │
│  1. session.getAttribute("user")       │ ← who is logged in?
│  2. check role == Admin                 │ ← gatekeeper
└───────────────┬────────────────────────┘
                ▼
┌────────────────────────────────────────┐
│ UserDAO.getAllUsers()                   │
│  uses DerbyConnection.getConnection()   │
│  reads derby.* from web.xml             │
└───────────────┬────────────────────────┘
                ▼
┌────────────────────────────────────────┐
│ Derby DB                                │
│  SELECT * FROM USERS                    │
│  ORDER BY ROLE, LAST_NAME               │
└───────────────┬────────────────────────┘
                │ List<User>
                ▼
┌────────────────────────────────────────┐
│ PdfReportBuilder                        │
│  • A4 landscape                         │
│  • filename USERLIST_yyyyMMddHHmmss.pdf │
│  • Content-Disposition: attachment      │
│  • PageNumberEventHandler attached      │
│       ↳ reads report.header/footer      │
│         from web.xml on every page      │
└───────────────┬────────────────────────┘
                │ stream PDF bytes
                ▼
┌────────┐
│Browser │  Save dialog appears → file downloaded
└────────┘
```

### 🟢 Example B — Time-Bound Enrollment Report

```
URL: /report/enrollments?scope=all&from=2026-01-01&to=2026-03-31

Servlet                            DAO                          MySQL
   │                                │                            │
   ├─ parse scope=all  ──── admin? ✓│                            │
   ├─ DateRangeParser ──── from/to ✓│                            │
   │                                │                            │
   ├──────────── call ─────────────►│                            │
   │     getAllEnrollmentsByDateRange(from, to)                  │
   │                                ├─── SELECT e.*, c.Name  ───►│
   │                                │    FROM Enrollments e      │
   │                                │    JOIN Courses c          │
   │                                │    WHERE e.Enroll_Date     │
   │                                │      BETWEEN ? AND ?       │
   │                                │◄────── List<Enrollment> ───┤
   │◄──────────── data ────────────┤                             │
   │                                │                            │
   ├─ PdfReportBuilder.startDocument()                            │
   ├─ PdfReportBuilder.createTable(headers, widths)               │
   ├─ for each row: table.addCell(...)                            │
   ├─ doc.add(table)                                              │
   └─ close() → bytes streamed to browser
```

### 🟠 Example C — Login Flow (planned)

```
┌─ Browser ─┐    POST /login    ┌─ LoginServlet ─┐
│ username  │ ──────────────────►│                │
│ password  │                    │ UserDAO        │
│ captcha   │                    │  .authenticate │
└───────────┘                    │   (u, p)       │
                                 │       │        │
                                 │       ▼        │
                                 │ PasswordHasher │
                                 │   .verify()    │
                                 │  salted SHA256 │
                                 └───────┬────────┘
                                         │
                                ┌────────┴────────┐
                                ▼                 ▼
                          if match:          if not:
                          session.setAttr     resp.sendRedirect
                            "user", u             "/login.jsp?err=1"
                          redirect /dashboard
```

---

## 5. The 3 DBMS — Why Each One?

| DBMS | Purpose | Tables | Connection Helper |
|---|---|---|---|
| **Derby (LoginDB)** | Authentication & user accounts. Embedded, low-traffic. | `USERS (USERNAME, PASSWORD, USERROLE)` | `DerbyConnection` |
| **MySQL (fap_activelearning)** | Core academic data — high-traffic, relational, the "domain". | `Courses`, `Course_Assignments`, `Course_Lessons`, `Enrollments`, `Teacher_Assignments`, `Course_Ratings` | `MySQLConnection` |
| **PostgreSQL 18** *(in progress by 3rd teammate)* | Third DBMS for the rubric. Folder + README scaffolded at `com/fap/dao/postgres/`. | TBD by teammate | `PostgresConnection` (to be added) |

### How modularity is preserved
Each DBMS lives behind its own *connection helper* in `com.fap.db.*` and its own
*DAO package* in `com.fap.dao.<dbms>.*`. To slot in a new DBMS:
1. Drop the JDBC JAR into `web/WEB-INF/lib/`.
2. Add `<dbms>.*` context-params to `web.xml`.
3. Write a `<Dbms>Connection.java` mirroring the existing two.
4. Write DAOs in `com/fap/dao/<dbms>/`.

No servlets or JSPs change. No model changes. Zero blast radius.

**Why separation matters:**
The spec requires multiple DBMSs *with context*. Separating auth from academic data demonstrates good systems analysis:
- Derby's embedded nature is great for sensitive auth tables that rarely change
- MySQL handles the bulk academic CRUD/reporting workload
- A third DBMS will demonstrate a context-appropriate choice (e.g., PostgreSQL for analytics, SQLite for portable course exports, Redis for session caching, etc.)

---

## 6. Security Model

| Concern | Solution |
|---|---|
| **SQL injection** | All queries use `PreparedStatement` with `?` placeholders. No string concatenation. |
| **Password storage** | AES/ECB/PKCS5Padding via `Servlets.Security` (single source of crypto). EncryptionKey lives in `web.xml`. UserDAO encrypts on INSERT/UPDATE and decrypts on read so callers always see plaintext. |
| **Sensitive credentials** | Stored in `web.xml` (Deployment Descriptor), never in source code. |
| **Session-based auth** | Servlets check `session.getAttribute("Role")` / `"UName"` on every request (set by `LoginServlet`). |
| **Role-based access** | `AuthFilter.requireAdmin()` returns HTTP 403 for non-Admins. |
| **Captcha** | Google reCAPTCHA v2 wired through `CaptchaServlet` before login is authorized. |
| **CSRF** | TBD — recommend a per-session token on POST forms. |

---

## 7. PDF Report Architecture

Every report follows the same recipe through `PdfReportBuilder`:

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Read web.xml params (header/footer) via ServletContext   │
├─────────────────────────────────────────────────────────────┤
│ 2. Set response headers:                                    │
│    Content-Type: application/pdf                            │
│    Content-Disposition: attachment; filename="..."          │
│    ←  this forces CLIENT-SIDE download (spec req. #9)       │
├─────────────────────────────────────────────────────────────┤
│ 3. Create Document(PageSize.A4.rotate())  ← landscape       │
├─────────────────────────────────────────────────────────────┤
│ 4. Attach PageNumberEventHandler:                           │
│    on every page → write:                                   │
│      • header (web.xml)                                     │
│      • "Generated by: <username>"                           │
│      • "Generated on: <date/time>"                          │
│      • footer (web.xml)                                     │
│      • "Page X of Y"                                        │
├─────────────────────────────────────────────────────────────┤
│ 5. PdfPTable handles pagination automatically — rows that   │
│    don't fit on the current page roll onto the next.        │
│    setHeaderRows(1) → column headers repeat on every page.  │
├─────────────────────────────────────────────────────────────┤
│ 6. Filename pattern: NAME_yyyyMMddHHmmss.pdf                │
│    (built by PdfReportBuilder.buildFilename())              │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. URL Map

| URL | Method | Purpose | Roles |
|---|---|---|---|
| `/report/users` | GET | User list PDF (50+ users, `*` for current admin) | Admin |
| `/report/courses` | GET | Course catalog PDF | Authenticated |
| `/report/enrollments?scope=all` | GET | All enrollments PDF | Admin |
| `/report/enrollments?scope=mine` | GET | Logged-in user's enrollments | Authenticated |
| `/report/teacher-assignments?scope=all` | GET | All teacher assignments | Admin |
| `/report/teacher-assignments?scope=mine` | GET | Logged-in teacher's assignments | Teacher |
| `/report/ratings?scope=all` | GET | All course ratings | Admin |
| `/report/ratings?scope=mine` | GET | Logged-in student's ratings | Student |

**Optional params on all routes:**
- `?from=YYYY-MM-DD&to=YYYY-MM-DD` → time-bound filtering

---

## 9. Dependency Graph

```
┌──────────────────────────────────────────────────┐
│ Report Servlets                                   │
│  ├─ depends on → DAOs                             │
│  ├─ depends on → models                           │
│  ├─ depends on → PdfReportBuilder                 │
│  ├─ depends on → PageNumberEventHandler           │
│  └─ depends on → DateRangeParser                  │
└──────────────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────────┐
│ DAOs (UserDAO, CourseDAO, ...)                    │
│  ├─ depends on → models                           │
│  └─ depends on → MySQLConnection / DerbyConnection│
└──────────────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────────┐
│ Connection Utilities (MySQLConnection, ...)       │
│  └─ depends on → ServletContext (web.xml)         │
└──────────────────────────────────────────────────┘

External JARs (web/WEB-INF/lib):
  ├─ derbyclient.jar              → Derby JDBC driver
  ├─ mysql-connector-j-9.6.0.jar  → MySQL JDBC driver
  └─ itextpdf-5.5.13.5.jar        → PDF generation
```

---

## 10. What's NOT Built Yet

| Feature | Status |
|---|---|
| Login servlet + JSP | ⛔ Pending — frontend teammate is on JSPs |
| Captcha integration | ⛔ Pending — Google reCAPTCHA recommended |
| Custom error pages (403.jsp, 404.jsp, 500.jsp) | ⛔ Pending — referenced in `web.xml` but JSPs don't exist |
| Admin CRUD UI | ⛔ Pending — frontend (Voyager-style table list) |
| Student/Teacher dashboards | ⛔ Pending — frontend |
| Third DBMS | ⛔ Pending — pick PostgreSQL / SQLite / etc. |
| User-side servlets (CourseServlet, EnrollServlet, etc.) | ⛔ Pending — separate from report servlets |

---

## 11. Quick Reference for Future Code

**Whenever you add a new servlet that needs the DB:**

```java
@WebServlet("/my-endpoint")
public class MyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Auth check
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2. Call DAO
        try {
            MyDAO dao = new MyDAO(getServletContext());
            List<SomeModel> data = dao.someQuery();

            // 3. Forward to JSP or write response
            req.setAttribute("data", data);
            req.getRequestDispatcher("/some.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
```

**Whenever you add a new DAO method:**

```java
public List<MyModel> myReportQuery(Date from, Date to) throws SQLException {
    List<MyModel> list = new ArrayList<>();
    String sql = "SELECT * FROM My_Table WHERE Some_Date BETWEEN ? AND ?";

    try (Connection conn = MySQLConnection.getConnection(context);
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, from);
        ps.setDate(2, to);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMyModel(rs));
        }
    }
    return list;
}
```
