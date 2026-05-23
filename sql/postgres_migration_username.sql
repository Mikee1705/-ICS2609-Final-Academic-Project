-- ============================================================
-- PostgreSQL Migration: add Username identity bridge
-- Run ONCE against the `postgres` database (public schema).
--
-- Backfills usernames in the same order as the Derby USERS seed
-- so the cross-DBMS bridge works on day one.
-- ============================================================

-- 1. Add the columns (nullable so the ALTER doesn't fail on populated tables)
ALTER TABLE Students ADD COLUMN IF NOT EXISTS Username VARCHAR(50);
ALTER TABLE Teachers ADD COLUMN IF NOT EXISTS Username VARCHAR(50);

-- 2. Backfill — Teachers (Postgres IDs 6–15)
UPDATE Teachers SET Username = 'tgarcia'     WHERE Teacher_ID = '6';
UPDATE Teachers SET Username = 'mtorres'     WHERE Teacher_ID = '7';
UPDATE Teachers SET Username = 'lvillanueva' WHERE Teacher_ID = '8';
UPDATE Teachers SET Username = 'jramos'      WHERE Teacher_ID = '9';
UPDATE Teachers SET Username = 'cmendoza'    WHERE Teacher_ID = '10';
UPDATE Teachers SET Username = 'pdelacruz'   WHERE Teacher_ID = '11';
UPDATE Teachers SET Username = 'nagusto'     WHERE Teacher_ID = '12';
UPDATE Teachers SET Username = 'eflores'     WHERE Teacher_ID = '13';
UPDATE Teachers SET Username = 'rbautista'   WHERE Teacher_ID = '14';
UPDATE Teachers SET Username = 'dcastro'     WHERE Teacher_ID = '15';

-- 3. Backfill — Students (Postgres IDs 16–55)
UPDATE Students SET Username = 'sdomingo'    WHERE Student_ID = '16';
UPDATE Students SET Username = 'larroyo'     WHERE Student_ID = '17';
UPDATE Students SET Username = 'amalabanan'  WHERE Student_ID = '18';
UPDATE Students SET Username = 'jfernandez'  WHERE Student_ID = '19';
UPDATE Students SET Username = 'eortiz'      WHERE Student_ID = '20';
UPDATE Students SET Username = 'cbalan'      WHERE Student_ID = '21';
UPDATE Students SET Username = 'rvalenzuela' WHERE Student_ID = '22';
UPDATE Students SET Username = 'mlopez'      WHERE Student_ID = '23';
UPDATE Students SET Username = 'hperez'      WHERE Student_ID = '24';
UPDATE Students SET Username = 'vsilva'      WHERE Student_ID = '25';
UPDATE Students SET Username = 'fjavier'     WHERE Student_ID = '26';
UPDATE Students SET Username = 'amorales'    WHERE Student_ID = '27';
UPDATE Students SET Username = 'jrosario'    WHERE Student_ID = '28';
UPDATE Students SET Username = 'rfajardo'    WHERE Student_ID = '29';
UPDATE Students SET Username = 'cnavarro'    WHERE Student_ID = '30';
UPDATE Students SET Username = 'btan'        WHERE Student_ID = '31';
UPDATE Students SET Username = 'mochoa'      WHERE Student_ID = '32';
UPDATE Students SET Username = 'rgallego'    WHERE Student_ID = '33';
UPDATE Students SET Username = 'lsantiago'   WHERE Student_ID = '34';
UPDATE Students SET Username = 'jvargas'     WHERE Student_ID = '35';
UPDATE Students SET Username = 'panonuevo'   WHERE Student_ID = '36';
UPDATE Students SET Username = 'mdiaz'       WHERE Student_ID = '37';
UPDATE Students SET Username = 'sgutierrez'  WHERE Student_ID = '38';
UPDATE Students SET Username = 'jaquino'     WHERE Student_ID = '39';
UPDATE Students SET Username = 'cdimaano'    WHERE Student_ID = '40';
UPDATE Students SET Username = 'rmagsino'    WHERE Student_ID = '41';
UPDATE Students SET Username = 'apangilinan' WHERE Student_ID = '42';
UPDATE Students SET Username = 'ksalvador'   WHERE Student_ID = '43';
UPDATE Students SET Username = 'mreyes2'     WHERE Student_ID = '44';
UPDATE Students SET Username = 'jjavellana'  WHERE Student_ID = '45';
UPDATE Students SET Username = 'grosales'    WHERE Student_ID = '46';
UPDATE Students SET Username = 'isandoval'   WHERE Student_ID = '47';
UPDATE Students SET Username = 'rmacapagal'  WHERE Student_ID = '48';
UPDATE Students SET Username = 'atorre'      WHERE Student_ID = '49';
UPDATE Students SET Username = 'lcabrera'    WHERE Student_ID = '50';
UPDATE Students SET Username = 'mtolentino'  WHERE Student_ID = '51';
UPDATE Students SET Username = 'cdyon'       WHERE Student_ID = '52';
UPDATE Students SET Username = 'rmanansala'  WHERE Student_ID = '53';
UPDATE Students SET Username = 'vapostol'    WHERE Student_ID = '54';
UPDATE Students SET Username = 'jrodriguez'  WHERE Student_ID = '55';

-- 4. Lock it in
ALTER TABLE Students ALTER COLUMN Username SET NOT NULL;
ALTER TABLE Students ADD CONSTRAINT uq_students_username UNIQUE (Username);

ALTER TABLE Teachers ALTER COLUMN Username SET NOT NULL;
ALTER TABLE Teachers ADD CONSTRAINT uq_teachers_username UNIQUE (Username);

-- ============================================================
-- VERIFY
-- ============================================================
-- SELECT Student_ID, Username, First_Name, Last_Name FROM Students ORDER BY Student_ID::INT;
-- SELECT Teacher_ID, Username, First_Name, Last_Name FROM Teachers ORDER BY Teacher_ID::INT;
