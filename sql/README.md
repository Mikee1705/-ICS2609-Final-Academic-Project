# FAP Database Setup Scripts

This folder contains **complete, self-contained** SQL scripts that build every database the FAP project needs — schema **and** data.

Run them in the order shown below.

---

## ▶ Run These In Order

| # | File | Database | What it does |
|---|---|---|---|
| 1 | `derby_setup.sql` | **Derby** `LoginDB` | Creates `USERS` table + 55 seed users (5 Admins, 10 Teachers, 40 Students). All passwords decrypt to `Password123`. |
| 2 | `postgres_setup.sql` | **PostgreSQL** `postgres` | Creates `Salutations`, `Students`, `Teachers`, `Student_Phones` + seed data with Username identity bridge. |
| 3 | `mysql_setup.sql` | **MySQL** `fap_activelearning` | Creates the 6 academic tables. (Run `mysql_seeder.sql` right after — it provides richer demo data.) |
| 4 | `mysql_seeder.sql` | **MySQL** `fap_activelearning` | Wipes and re-seeds Courses, Lessons, Assignments, Enrollments (55 rows), Teacher_Assignments (11 rows), Course_Ratings (24 rows) using real Postgres IDs. |

After step 4, the system is fully populated and ready to demo.

---

## 🛠 Legacy / Optional Scripts

| File | Use only if... |
|---|---|
| `mysql_migration_varchar_ids.sql` | You ran an OLD `mysql_setup.sql` (pre-Postgres-integration) where `Student_ID`/`Teacher_ID` were `INT`. Converts them to `VARCHAR(50)` to match Postgres. New installs **do not** need this. |

---

## 🔐 Credentials (default)

| DBMS | Database | User | Password | Port |
|---|---|---|---|---|
| Derby | `LoginDB` | `APP` | `APP` | 1527 |
| PostgreSQL | `postgres` | `postgres` | *(your install password)* | 5432 |
| MySQL | `fap_activelearning` | `root` | *(blank — XAMPP default)* | 3306 |

If your installs use different passwords, **update `web/WEB-INF/web.xml`** to match.

---

## 🪪 Default Login Account

Once the seeds are loaded, you can log in immediately:

| Username | Password | Role |
|---|---|---|
| `admin` | `Password123` | Admin |

Any of the other 54 seed users (mreyes, tgarcia, sdomingo, etc.) also work with `Password123`.

---

## ✅ Verification

After running all four scripts, sanity-check each DB:

```sql
-- Derby
SELECT COUNT(*) FROM USERS;                           -- 55

-- PostgreSQL
SELECT COUNT(*) FROM Salutations;                     -- 5
SELECT COUNT(*) FROM Teachers;                        -- 10
SELECT COUNT(*) FROM Students;                        -- 40

-- MySQL
USE fap_activelearning;
SELECT COUNT(*) FROM Courses;                         -- 5
SELECT COUNT(*) FROM Enrollments;                     -- 55
SELECT COUNT(*) FROM Teacher_Assignments;             -- 11
SELECT COUNT(*) FROM Course_Ratings;                  -- 24
```

---

## 🧠 Why these are self-contained

Earlier versions split things into a "setup" script + a Java utility (`DerbySeed.java`) for the password hashes. That worked but added a manual step. AES/ECB is deterministic, so we pre-computed the ciphertext for `"Password123"` (`qLf1PBQ6PUU4JJi90LSn2Q==`) and inlined it — meaning the SQL files alone are now enough to bring a fresh machine up to a working state.

`DerbySeed.java` still exists in `src/java/com/fap/util/` for **future** use (when you want to add new users via SQL with a different default password).
