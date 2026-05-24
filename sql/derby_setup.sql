-- ============================================================
-- FAP Derby Setup — LoginDB.USERS (COMPLETE / SELF-CONTAINED)
--
-- Run this ONCE on a fresh LoginDB to create the USERS table and
-- seed all 55 demo users (5 Admins, 10 Teachers, 40 Students).
--
-- The PASSWORD column stores AES/ECB/PKCS5Padding ciphertext
-- (Base64-encoded) — same algorithm as Servlets.Security.
--   Plaintext password for EVERY seed user: "Password123"
--   EncryptionKey:  0bj3Ct1f!c@t1on$       ← matches web.xml
--
-- Because AES/ECB is deterministic, the same key + plaintext always
-- produces the same ciphertext (qLf1PBQ6PUU4JJi90LSn2Q==), so this
-- script works the same on every machine without running Java first.
--
-- USAGE (NetBeans):
--   1. Start the Java DB (Derby) server.
--   2. Create the connection:  jdbc:derby://localhost:1527/LoginDB;create=true
--                               user=APP  password=APP
--   3. Right-click the connection → Execute Command.
--   4. Paste this entire file → run (Ctrl + Shift + F9).
--   5. Verify:  SELECT COUNT(*) FROM USERS;   -- expected: 55
-- ============================================================

-- ----- Schema -----
CREATE TABLE USERS (
    USERNAME    VARCHAR(50)  NOT NULL,
    PASSWORD    VARCHAR(255) NOT NULL,
    USERROLE    VARCHAR(20)  NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (USERNAME),
    CONSTRAINT CHK_ROLE CHECK (USERROLE IN ('Admin','Teacher','Student','Guest'))
);

-- ============================================================
-- SEED DATA — 5 Admins, 10 Teachers, 40 Students
-- All passwords decrypt to "Password123".
-- ============================================================

-- ----- 5 Admins -----
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('admin',       'qLf1PBQ6PUU4JJi90LSn2Q==', 'Admin');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mreyes',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Admin');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jcruz',       'qLf1PBQ6PUU4JJi90LSn2Q==', 'Admin');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('asantos',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Admin');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rlim',        'qLf1PBQ6PUU4JJi90LSn2Q==', 'Admin');

-- ----- 10 Teachers (match Postgres Teacher_IDs 6–15) -----
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('tgarcia',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mtorres',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('lvillanueva', 'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jramos',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('cmendoza',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('pdelacruz',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('nagusto',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('eflores',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rbautista',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('dcastro',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Teacher');

-- ----- 40 Students (match Postgres Student_IDs 16–55) -----
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('sdomingo',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('larroyo',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('amalabanan',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jfernandez',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('eortiz',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('cbalan',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rvalenzuela', 'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mlopez',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('hperez',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('vsilva',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('fjavier',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('amorales',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jrosario',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rfajardo',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('cnavarro',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('btan',        'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mochoa',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rgallego',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('lsantiago',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jvargas',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('panonuevo',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mdiaz',       'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('sgutierrez',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jaquino',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('cdimaano',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rmagsino',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('apangilinan', 'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('ksalvador',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mreyes2',     'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jjavellana',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('grosales',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('isandoval',   'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rmacapagal',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('atorre',      'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('lcabrera',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('mtolentino',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('cdyon',       'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('rmanansala',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('vapostol',    'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');
INSERT INTO USERS (USERNAME, PASSWORD, USERROLE) VALUES ('jrodriguez',  'qLf1PBQ6PUU4JJi90LSn2Q==', 'Student');

-- ============================================================
-- VERIFICATION
-- ============================================================
-- SELECT COUNT(*) FROM USERS;                                 -- 55
-- SELECT USERROLE, COUNT(*) FROM USERS GROUP BY USERROLE;     -- 5 / 10 / 40
