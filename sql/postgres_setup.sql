-- ============================================================
-- FAP PostgreSQL Setup — postgres.public.*  (COMPLETE)
--
-- Run this ONCE on the default `postgres` database to create the
-- user-profile schema and seed it with 10 Teachers + 40 Students,
-- including the Username identity-bridge column that links to the
-- Derby USERS table.
--
-- ID convention shared with Derby and MySQL:
--   • Teacher_IDs '6'  – '15'   ← matches Derby usernames in order
--   • Student_IDs '16' – '55'   ← matches Derby usernames in order
--
-- USAGE (NetBeans):
--   1. Make sure PostgreSQL is running.
--   2. Connect to:  jdbc:postgresql://localhost:5432/postgres
--                    user=postgres  password=<your install password>
--   3. Right-click connection → Execute Command.
--   4. Paste this entire file → run.
-- ============================================================

-- Clean slate so this script is re-runnable
DROP TABLE IF EXISTS Student_Phones CASCADE;
DROP TABLE IF EXISTS Students       CASCADE;
DROP TABLE IF EXISTS Teachers       CASCADE;
DROP TABLE IF EXISTS Salutations    CASCADE;

-- ============================================================
-- SCHEMA
-- ============================================================
CREATE TABLE Salutations (
    Salutation_ID SERIAL PRIMARY KEY,
    Title VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE Students (
    Student_ID        VARCHAR(50)  PRIMARY KEY,
    Salutation_ID     INT,
    First_Name        VARCHAR(100) NOT NULL,
    Last_Name         VARCHAR(100) NOT NULL,
    Email             VARCHAR(255) NOT NULL UNIQUE,
    Funding           VARCHAR(100),
    Registration_Date DATE         NOT NULL,
    Username          VARCHAR(50)  NOT NULL UNIQUE,   -- identity bridge ↔ Derby
    CONSTRAINT fk_student_salutation FOREIGN KEY (Salutation_ID) REFERENCES Salutations(Salutation_ID)
);

CREATE TABLE Student_Phones (
    Phone_ID     SERIAL PRIMARY KEY,
    Student_ID   VARCHAR(50) NOT NULL,
    Phone_Number VARCHAR(20) NOT NULL,
    Phone_Type   VARCHAR(20),
    CONSTRAINT fk_phone_student FOREIGN KEY (Student_ID) REFERENCES Students(Student_ID) ON DELETE CASCADE,
    CONSTRAINT unique_student_phone UNIQUE (Student_ID, Phone_Number)
);

CREATE TABLE Teachers (
    Teacher_ID    VARCHAR(50)  PRIMARY KEY,
    Salutation_ID INT,
    First_Name    VARCHAR(100) NOT NULL,
    Last_Name     VARCHAR(100) NOT NULL,
    Username      VARCHAR(50)  NOT NULL UNIQUE,       -- identity bridge ↔ Derby
    CONSTRAINT fk_teacher_salutation FOREIGN KEY (Salutation_ID) REFERENCES Salutations(Salutation_ID)
);

-- ============================================================
-- SEED — Salutations
-- ============================================================
INSERT INTO Salutations (Title) VALUES
    ('Mr.'), ('Ms.'), ('Mrs.'), ('Dr.'), ('Prof.');

-- ============================================================
-- SEED — Teachers (IDs 6–15, matches Derby usernames in seed order)
-- ============================================================
INSERT INTO Teachers (Teacher_ID, Salutation_ID, First_Name, Last_Name, Username) VALUES
('6',  2, 'Teresa',  'Garcia',     'tgarcia'),
('7',  1, 'Miguel',  'Torres',     'mtorres'),
('8',  2, 'Liza',    'Villanueva', 'lvillanueva'),
('9',  1, 'Jose',    'Ramos',      'jramos'),
('10', 2, 'Carla',   'Mendoza',    'cmendoza'),
('11', 1, 'Pedro',   'Dela Cruz',  'pdelacruz'),
('12', 2, 'Nina',    'Agusto',     'nagusto'),
('13', 1, 'Eduardo', 'Flores',     'eflores'),
('14', 2, 'Rosa',    'Bautista',   'rbautista'),
('15', 1, 'Daniel',  'Castro',     'dcastro');

-- ============================================================
-- SEED — Students (IDs 16–55, matches Derby usernames in seed order)
-- ============================================================
INSERT INTO Students (Student_ID, Salutation_ID, First_Name, Last_Name, Email, Funding, Registration_Date, Username) VALUES
('16', 2, 'Sofia',     'Domingo',     'sdomingo@student.ph',   'Self-Funded', '2026-01-10', 'sdomingo'),
('17', 1, 'Luis',      'Arroyo',      'larroyo@student.ph',    'Self-Funded', '2026-01-12', 'larroyo'),
('18', 2, 'Anna',      'Malabanan',   'amalaba@student.ph',    'Scholarship', '2026-01-15', 'amalabanan'),
('19', 1, 'Jorge',     'Fernandez',   'jfernandez@student.ph', 'Self-Funded', '2026-01-16', 'jfernandez'),
('20', 2, 'Elena',     'Ortiz',       'eortiz@student.ph',     'Self-Funded', '2026-01-20', 'eortiz'),
('21', 1, 'Carlos',    'Balan',       'cbalan@student.ph',     'Company-Paid','2026-01-22', 'cbalan'),
('22', 2, 'Rita',      'Valenzuela',  'rvalen@student.ph',     'Self-Funded', '2026-01-25', 'rvalenzuela'),
('23', 1, 'Mark',      'Lopez',       'mlopez@student.ph',     'Scholarship', '2026-02-01', 'mlopez'),
('24', 2, 'Hannah',    'Perez',       'hperez@student.ph',     'Self-Funded', '2026-02-04', 'hperez'),
('25', 1, 'Vincent',   'Silva',       'vsilva@student.ph',     'Company-Paid','2026-02-05', 'vsilva'),
('26', 2, 'Fatima',    'Javier',      'fjavier@student.ph',    'Self-Funded', '2026-02-06', 'fjavier'),
('27', 1, 'Antonio',   'Morales',     'amorales@student.ph',   'Self-Funded', '2026-02-10', 'amorales'),
('28', 2, 'Jasmin',    'Del Rosario', 'jrosario@student.ph',   'Self-Funded', '2026-02-11', 'jrosario'),
('29', 1, 'Ramon',     'Fajardo',     'rfajardo@student.ph',   'Self-Funded', '2026-02-14', 'rfajardo'),
('30', 2, 'Christine', 'Navarro',     'cnavarro@student.ph',   'Company-Paid','2026-02-15', 'cnavarro'),
('31', 1, 'Bryan',     'Tan',         'btan@student.ph',       'Self-Funded', '2026-02-18', 'btan'),
('32', 2, 'Monica',    'Ochoa',       'mochoa@student.ph',     'Scholarship', '2026-02-20', 'mochoa'),
('33', 1, 'Rafael',    'Gallego',     'rgallego@student.ph',   'Self-Funded', '2026-02-22', 'rgallego'),
('34', 2, 'Lorraine',  'Santiago',    'lsantiago@student.ph',  'Self-Funded', '2026-02-25', 'lsantiago'),
('35', 1, 'Jericho',   'Vargas',      'jvargas@student.ph',    'Company-Paid','2026-03-01', 'jvargas'),
('36', 2, 'Patricia',  'Anonuevo',    'panonue@student.ph',    'Self-Funded', '2026-03-02', 'panonuevo'),
('37', 1, 'Marco',     'Diaz',        'mdiaz@student.ph',      'Self-Funded', '2026-03-05', 'mdiaz'),
('38', 2, 'Selena',    'Gutierrez',   'sgutier@student.ph',    'Self-Funded', '2026-03-06', 'sgutierrez'),
('39', 1, 'Joseph',    'Aquino',      'jaquino@student.ph',    'Scholarship', '2026-03-10', 'jaquino'),
('40', 2, 'Cherry',    'Dimaano',     'cdimaano@student.ph',   'Self-Funded', '2026-03-12', 'cdimaano'),
('41', 1, 'Rico',      'Magsino',     'rmagsino@student.ph',   'Self-Funded', '2026-03-15', 'rmagsino'),
('42', 2, 'Angeline',  'Pangilinan',  'apangili@student.ph',   'Company-Paid','2026-03-18', 'apangilinan'),
('43', 1, 'Kevin',     'Salvador',    'ksalvador@student.ph',  'Self-Funded', '2026-03-20', 'ksalvador'),
('44', 2, 'Mira',      'Reyes',       'mreyes2@student.ph',    'Self-Funded', '2026-03-22', 'mreyes2'),
('45', 1, 'Jacob',     'Javellana',   'jjavella@student.ph',   'Self-Funded', '2026-03-25', 'jjavellana'),
('46', 2, 'Grace',     'Rosales',     'grosales@student.ph',   'Scholarship', '2026-03-28', 'grosales'),
('47', 1, 'Ian',       'Sandoval',    'isandoval@student.ph',  'Self-Funded', '2026-04-01', 'isandoval'),
('48', 2, 'Reina',     'Macapagal',   'rmacapag@student.ph',   'Company-Paid','2026-04-05', 'rmacapagal'),
('49', 1, 'Aaron',     'Torre',       'atorre@student.ph',     'Self-Funded', '2026-04-08', 'atorre'),
('50', 2, 'Lara',      'Cabrera',     'lcabrera@student.ph',   'Self-Funded', '2026-04-10', 'lcabrera'),
('51', 1, 'Manuel',    'Tolentino',   'mtolentino@student.ph', 'Self-Funded', '2026-04-12', 'mtolentino'),
('52', 2, 'Chloe',     'Dyon',        'cdyon@student.ph',      'Scholarship', '2026-04-15', 'cdyon'),
('53', 1, 'Ronald',    'Manansala',   'rmanansa@student.ph',   'Self-Funded', '2026-04-18', 'rmanansala'),
('54', 2, 'Victoria',  'Apostol',     'vapostol@student.ph',   'Company-Paid','2026-04-20', 'vapostol'),
('55', 2, 'Janine',    'Rodriguez',   'jrodriguez@student.ph', 'Self-Funded', '2026-04-25', 'jrodriguez');

-- ============================================================
-- VERIFICATION
-- ============================================================
-- SELECT COUNT(*) FROM Salutations;  -- 5
-- SELECT COUNT(*) FROM Teachers;     -- 10
-- SELECT COUNT(*) FROM Students;     -- 40
