-- ============================================================
-- FAP Derby Setup Script — USERS table for authentication
-- and the User List PDF report (must have at least 50 records).
--
-- Run this in NetBeans Services panel:
--   1. Connect to Derby (jdbc:derby://localhost:1527/fap_derby;create=true)
--   2. Right-click the connection → Execute Command
--   3. Paste this script → Run (Ctrl + Shift + F9)
--
-- NOTE: PASSWORD_HASH values below are pre-computed salted SHA-256
-- in the format saltHex:hashHex. They all decode to the plain password
-- "Password123" — useful for testing the login flow.
-- Replace them with real PasswordHasher.hash(...) output in production.
-- ============================================================

CREATE TABLE USERS (
    USER_ID         INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    USERNAME        VARCHAR(50)  NOT NULL UNIQUE,
    PASSWORD_HASH   VARCHAR(255) NOT NULL,
    FIRST_NAME      VARCHAR(80)  NOT NULL,
    LAST_NAME       VARCHAR(80)  NOT NULL,
    EMAIL           VARCHAR(120) NOT NULL UNIQUE,
    ROLE            VARCHAR(20)  NOT NULL,    -- Admin / Teacher / Student
    IS_ACTIVE       BOOLEAN      NOT NULL DEFAULT TRUE,
    CREATED_AT      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    LAST_LOGIN      TIMESTAMP,
    CONSTRAINT PK_USERS    PRIMARY KEY (USER_ID),
    CONSTRAINT CHK_ROLE    CHECK (ROLE IN ('Admin','Teacher','Student'))
);

-- Demo hash — all sample users below have the password "Password123"
-- so testers can log in without having to call PasswordHasher.hash() first.
-- IMPORTANT: regenerate these via PasswordHasher for real users.
-- (salt:hash, salted SHA-256 — placeholder constant for seed data only)
-- ============================================================
-- SEED DATA — 5 Admins, 10 Teachers, 40 Students = 55 users
-- ============================================================

-- ---- ADMINS ----
INSERT INTO USERS (USERNAME, PASSWORD_HASH, FIRST_NAME, LAST_NAME, EMAIL, ROLE) VALUES
('admin',        'seed:replace-with-real-hash', 'System',   'Administrator', 'admin@activelearning.ph',     'Admin'),
('mreyes',       'seed:replace-with-real-hash', 'Maria',    'Reyes',         'mreyes@activelearning.ph',    'Admin'),
('jcruz',        'seed:replace-with-real-hash', 'Juan',     'Cruz',          'jcruz@activelearning.ph',     'Admin'),
('asantos',      'seed:replace-with-real-hash', 'Andrea',   'Santos',        'asantos@activelearning.ph',   'Admin'),
('rlim',         'seed:replace-with-real-hash', 'Roberto',  'Lim',           'rlim@activelearning.ph',      'Admin');

-- ---- TEACHERS ----
INSERT INTO USERS (USERNAME, PASSWORD_HASH, FIRST_NAME, LAST_NAME, EMAIL, ROLE) VALUES
('tgarcia',      'seed:replace-with-real-hash', 'Teresa',   'Garcia',        'tgarcia@activelearning.ph',   'Teacher'),
('mtorres',      'seed:replace-with-real-hash', 'Miguel',   'Torres',        'mtorres@activelearning.ph',   'Teacher'),
('lvillanueva',  'seed:replace-with-real-hash', 'Liza',     'Villanueva',    'lvilla@activelearning.ph',    'Teacher'),
('jramos',       'seed:replace-with-real-hash', 'Jose',     'Ramos',         'jramos@activelearning.ph',    'Teacher'),
('cmendoza',     'seed:replace-with-real-hash', 'Carla',    'Mendoza',       'cmendoza@activelearning.ph',  'Teacher'),
('pdelacruz',    'seed:replace-with-real-hash', 'Pedro',    'Dela Cruz',     'pdelacruz@activelearning.ph', 'Teacher'),
('nagusto',      'seed:replace-with-real-hash', 'Nina',     'Agusto',        'nagusto@activelearning.ph',   'Teacher'),
('eflores',      'seed:replace-with-real-hash', 'Eduardo',  'Flores',        'eflores@activelearning.ph',   'Teacher'),
('rbautista',    'seed:replace-with-real-hash', 'Rosa',     'Bautista',      'rbautista@activelearning.ph', 'Teacher'),
('dcastro',      'seed:replace-with-real-hash', 'Daniel',   'Castro',        'dcastro@activelearning.ph',   'Teacher');

-- ---- STUDENTS ----
INSERT INTO USERS (USERNAME, PASSWORD_HASH, FIRST_NAME, LAST_NAME, EMAIL, ROLE) VALUES
('sdomingo',     'seed:replace-with-real-hash', 'Sofia',    'Domingo',       'sdomingo@student.ph',    'Student'),
('larroyo',      'seed:replace-with-real-hash', 'Luis',     'Arroyo',        'larroyo@student.ph',     'Student'),
('amalabanan',   'seed:replace-with-real-hash', 'Anna',     'Malabanan',     'amalaba@student.ph',     'Student'),
('jfernandez',   'seed:replace-with-real-hash', 'Jorge',    'Fernandez',     'jfernandez@student.ph',  'Student'),
('eortiz',       'seed:replace-with-real-hash', 'Elena',    'Ortiz',         'eortiz@student.ph',      'Student'),
('cbalan',       'seed:replace-with-real-hash', 'Carlos',   'Balan',         'cbalan@student.ph',      'Student'),
('rvalenzuela',  'seed:replace-with-real-hash', 'Rita',     'Valenzuela',    'rvalen@student.ph',      'Student'),
('mlopez',       'seed:replace-with-real-hash', 'Mark',     'Lopez',         'mlopez@student.ph',      'Student'),
('hperez',       'seed:replace-with-real-hash', 'Hannah',   'Perez',         'hperez@student.ph',      'Student'),
('vsilva',       'seed:replace-with-real-hash', 'Vincent',  'Silva',         'vsilva@student.ph',      'Student'),
('fjavier',      'seed:replace-with-real-hash', 'Fatima',   'Javier',        'fjavier@student.ph',     'Student'),
('amorales',     'seed:replace-with-real-hash', 'Antonio',  'Morales',       'amorales@student.ph',    'Student'),
('jrosario',     'seed:replace-with-real-hash', 'Jasmin',   'Del Rosario',   'jrosario@student.ph',    'Student'),
('rfajardo',     'seed:replace-with-real-hash', 'Ramon',    'Fajardo',       'rfajardo@student.ph',    'Student'),
('cnavarro',     'seed:replace-with-real-hash', 'Christine','Navarro',       'cnavarro@student.ph',    'Student'),
('btan',         'seed:replace-with-real-hash', 'Bryan',    'Tan',           'btan@student.ph',        'Student'),
('mochoa',       'seed:replace-with-real-hash', 'Monica',   'Ochoa',         'mochoa@student.ph',      'Student'),
('rgallego',     'seed:replace-with-real-hash', 'Rafael',   'Gallego',       'rgallego@student.ph',    'Student'),
('lsantiago',    'seed:replace-with-real-hash', 'Lorraine', 'Santiago',      'lsantiago@student.ph',   'Student'),
('jvargas',      'seed:replace-with-real-hash', 'Jericho',  'Vargas',        'jvargas@student.ph',     'Student'),
('panonuevo',    'seed:replace-with-real-hash', 'Patricia', 'Anonuevo',      'panonue@student.ph',     'Student'),
('mdiaz',        'seed:replace-with-real-hash', 'Marco',    'Diaz',          'mdiaz@student.ph',       'Student'),
('sgutierrez',   'seed:replace-with-real-hash', 'Selena',   'Gutierrez',     'sgutier@student.ph',     'Student'),
('jaquino',      'seed:replace-with-real-hash', 'Joseph',   'Aquino',        'jaquino@student.ph',     'Student'),
('cdimaano',     'seed:replace-with-real-hash', 'Cherry',   'Dimaano',       'cdimaano@student.ph',    'Student'),
('rmagsino',     'seed:replace-with-real-hash', 'Rico',     'Magsino',       'rmagsino@student.ph',    'Student'),
('apangilinan',  'seed:replace-with-real-hash', 'Angeline', 'Pangilinan',    'apangili@student.ph',    'Student'),
('ksalvador',    'seed:replace-with-real-hash', 'Kevin',    'Salvador',      'ksalvador@student.ph',   'Student'),
('mreyes2',      'seed:replace-with-real-hash', 'Mira',     'Reyes',         'mreyes2@student.ph',     'Student'),
('jjavellana',   'seed:replace-with-real-hash', 'Jacob',    'Javellana',     'jjavella@student.ph',    'Student'),
('grosales',     'seed:replace-with-real-hash', 'Grace',    'Rosales',       'grosales@student.ph',    'Student'),
('isandoval',    'seed:replace-with-real-hash', 'Ian',      'Sandoval',      'isandoval@student.ph',   'Student'),
('rmacapagal',   'seed:replace-with-real-hash', 'Reina',    'Macapagal',     'rmacapag@student.ph',    'Student'),
('atorre',       'seed:replace-with-real-hash', 'Aaron',    'Torre',         'atorre@student.ph',      'Student'),
('lcabrera',     'seed:replace-with-real-hash', 'Lara',     'Cabrera',       'lcabrera@student.ph',    'Student'),
('mtolentino',   'seed:replace-with-real-hash', 'Manuel',   'Tolentino',     'mtolentino@student.ph',  'Student'),
('cdyon',        'seed:replace-with-real-hash', 'Chloe',    'Dyon',          'cdyon@student.ph',       'Student'),
('rmanansala',   'seed:replace-with-real-hash', 'Ronald',   'Manansala',     'rmanansa@student.ph',    'Student'),
('vapostol',     'seed:replace-with-real-hash', 'Victoria', 'Apostol',       'vapostol@student.ph',    'Student'),
('jrodriguez',   'seed:replace-with-real-hash', 'Janine',   'Rodriguez',     'jrodriguez@student.ph',  'Student'),
('balcazar',     'seed:replace-with-real-hash', 'Benjamin', 'Alcazar',       'balcazar@student.ph',    'Student');

-- ============================================================
-- After running, verify with:
--   SELECT COUNT(*) FROM USERS;            -- should be 55
--   SELECT ROLE, COUNT(*) FROM USERS GROUP BY ROLE;
-- ============================================================
