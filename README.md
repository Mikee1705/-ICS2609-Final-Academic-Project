# FAP — Final Academic Project
### Active Learning, Inc. Course Management System
**ICS2609 | Web Application Development**

---

## What We Did Today

### 1. Fixed JAR Dependency Issue
Moved all required JAR files **into the project** so teammates no longer need to manually download them.
- Copied `derbyclient.jar`, `itextpdf-5.5.13.5.jar`, and `mysql-connector-j-9.6.0.jar` into `web/WEB-INF/lib/`
- Updated `nbproject/project.properties` to use **relative paths** instead of absolute machine-specific paths
- Added `.gitignore` to prevent build/dist folders from being committed

---

### 2. Created MySQL Database (DBMS 3 — fap_activelearning)
Set up the third DBMS as required by the FAP specs (Derby + MySQL + one more).
The MySQL database handles the **Active Learning course context**.

#### Tables Created:
| Table | Purpose |
|---|---|
| `Courses` | Core course catalog (Course_Code, Name, Category, Level, Duration, etc.) |
| `Course_Assignments` | Assignments linked to a course via FK (Type, Due_Date, Max/Passing Score) |
| `Course_Lessons` | Lessons/modules per course (Type, Duration, Order, Resource URL) |
| `Enrollments` | Junction table — which student is in which course (key field: `Enrollment_Date` for time-bound reports) |
| `Teacher_Assignments` | Junction table — which teacher handles which course (Role: Primary, Co-Instructor, TA) |
| `Course_Ratings` | Junction table — student ratings per course (1–5 score, review text, recommendation) |

All tables follow **3NF**. Foreign keys cascade on delete/update from `Courses`.

---

### 3. Created Project File Structure

#### New Folders & Files Added:
```
sql/
  mysql_setup.sql              ← Run this to create all 6 tables + sample data

src/java/com/fap/
  db/
    MySQLConnection.java       ← Gets MySQL connection from web.xml credentials
    DerbyConnection.java       ← Gets Derby connection from web.xml credentials
  model/
    Course.java
    CourseAssignment.java
    CourseLesson.java
    Enrollment.java
    TeacherAssignment.java
    CourseRating.java
  dao/mysql/
    CourseDAO.java             ← CRUD + time-bound report queries for Courses/Assignments/Lessons
    EnrollmentDAO.java         ← CRUD + time-bound report queries for Enrollments
    TeacherAssignmentDAO.java  ← CRUD + time-bound report queries for Teacher Assignments
    CourseRatingDAO.java       ← CRUD + analytics queries for Course Ratings

web/WEB-INF/
  web.xml                      ← Deployment descriptor: MySQL/Derby credentials, error pages, report headers
  lib/
    derbyclient.jar
    itextpdf-5.5.13.5.jar
    mysql-connector-j-9.6.0.jar
```

---

### 4. Credentials Stored in Deployment Descriptor
As required by the FAP specs, all sensitive data is stored in `web/WEB-INF/web.xml`:
- MySQL URL, driver, username, password
- Derby URL, driver, username, password
- PDF report header and footer text

---

## How to Set Up MySQL on Your Machine

### Step 1 — Install XAMPP (if not yet installed)
Download from: https://www.apachefriends.org/
Make sure to install with the **MySQL** component included.

### Step 2 — Start MySQL
1. Open **XAMPP Control Panel**
2. Click **Start** next to **MySQL**
3. Wait for the green light on port **3306**

### Step 3 — Add the MySQL Driver to NetBeans
1. In NetBeans → **Services** tab → right-click **Drivers** → **New Driver**
2. Click **Add** → navigate to:
   ```
   web/WEB-INF/lib/mysql-connector-j-9.6.0.jar
   ```
3. Driver class: `com.mysql.cj.jdbc.Driver`
4. Name it `MySQL (Connector/J 9.6)` → click **OK**

### Step 4 — Create the Database Connection in NetBeans
1. Right-click **Databases** → **New Connection**
2. Select the driver you just added
3. Fill in:
   - **Host:** `localhost`
   - **Port:** `3306`
   - **Database:** `fap_activelearning`
   - **Username:** `root`
   - **Password:** *(leave blank — XAMPP default)*
4. Click **Test Connection** → should show a green checkmark ✅
5. Click **Finish**

### Step 5 — Run the Setup Script
1. Right-click the `fap_activelearning` connection → **Execute Command**
2. Open `sql/mysql_setup.sql` from the project
3. Copy and paste the full contents into the editor
4. Press **Ctrl + Shift + F9** to run
5. Right-click **Tables** under `fap_activelearning` → **Refresh**
6. All 6 tables should now appear ✅

---

## Resolving Merge Conflicts on web.xml
If you get a conflict on `web/WEB-INF/web.xml` after pulling:
- **Keep the Current Change** (our branch) — it has all the credentials and config
- **Discard the Incoming Change** from main — it is an empty shell
- Delete the conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`) and save

---

## Notes
- MySQL password in `web.xml` is set to **blank** (XAMPP default). If you have a root password set, update it in `web/WEB-INF/web.xml` under `mysql.password`
- The `build/` and `dist/` folders are now ignored by git — do not commit them
- Always do a **Clean and Build** in NetBeans after pulling
