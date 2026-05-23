-- ============================================================
-- FAP Derby Setup — LoginDB.USERS
--
-- This schema mirrors what Servlets.LoginServlet expects, so anyone
-- (the Login flow, UserDAO, the User List PDF report) is reading
-- from the same source of truth.
--
-- Columns:
--   USERNAME   PK
--   PASSWORD   AES/ECB/PKCS5Padding-encrypted via Servlets.Security
--              (EncryptionKey lives in web.xml)
--   USERROLE   'Admin' / 'Teacher' / 'Student' / 'Guest'
--
-- Run this in NetBeans:
--   1. Start the Derby (Java DB) server.
--   2. Connect to:  jdbc:derby://localhost:1527/LoginDB;create=true
--                   user APP / password APP
--   3. Right-click the connection → Execute Command
--   4. Paste this script → run (Ctrl + Shift + F9)
-- ============================================================

CREATE TABLE USERS (
    USERNAME    VARCHAR(50)  NOT NULL,
    PASSWORD    VARCHAR(255) NOT NULL,
    USERROLE    VARCHAR(20)  NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (USERNAME),
    CONSTRAINT CHK_ROLE CHECK (USERROLE IN ('Admin','Teacher','Student','Guest'))
);

-- ============================================================
-- SEED DATA — 55 users (5 Admin / 10 Teacher / 40 Student)
--
-- IMPORTANT: PASSWORD values below must be AES-encrypted using the
-- SAME EncryptionKey that lives in web.xml (currently:
-- "0bj3Ct1f!c@t1on$"). You CANNOT type plaintext passwords here —
-- the LoginServlet will try to decrypt them and fail.
--
-- ➤ TO GENERATE THE INSERT STATEMENTS:
--
--   1. Make sure the project is deployed at least once so GlassFish
--      can run the encryption code.
--   2. Right-click  src/java/com/fap/util/DerbySeed.java  →  Run File.
--      (It has a main() that prints all 55 INSERT statements with
--       properly AES-encrypted passwords. Default plaintext is
--       "Password123" — change inside DerbySeed if you want.)
--   3. Copy its console output into the NetBeans SQL editor below
--      this comment block and execute.
--
-- After insert, every demo user can log in with the password
-- "Password123" until you reset their passwords through the admin UI.
-- ============================================================

-- ⬇️  PASTE GENERATED INSERT STATEMENTS HERE  ⬇️



-- ============================================================
-- VERIFICATION
-- ============================================================
-- SELECT COUNT(*) FROM USERS;                                    -- 55
-- SELECT USERROLE, COUNT(*) FROM USERS GROUP BY USERROLE;        -- 5/10/40
