-- Sample data for the Smart Clinic `cms` MySQL database.
--
-- Run this after the Spring Boot app has started once, so Hibernate has created the tables:
--   mysql -u <user> -p cms < database/sample-data.sql
--
-- Passwords are stored as BCrypt hashes. The plain-text passwords for trying out logins are:
--   admins:   admin@1234
--   doctors:  doctor@123
--   patients: patient@123
--
-- Appointment dates are relative to the day the script is run, so there are always past
-- (completed), today's, and upcoming (scheduled) appointments to work with.
-- Rows use fixed IDs so the MongoDB prescriptions can reference appointments by ID; running the
-- script twice stops at the first duplicate ID and the transaction is rolled back.

START TRANSACTION;

INSERT INTO admin (id, username, password) VALUES
  (1, 'admin', '$2b$10$XP1TULq2hLE3sg1jt9/jx.qOepNjLDG1Dm78FQ82dAIQrWa9qGqIy'),
  (2, 'clinic_manager', '$2b$10$EVtAIKIuxuCUp0FsQEr/fuw9EsNYxiLZI1um8lvpHdqxF6ezaaSJ2');

INSERT INTO doctor (id, name, specialty, email, password, phone) VALUES
  (1, 'Dr. Emily Adams', 'Cardiologist', 'emily.adams@smartclinic.com', '$2b$10$uDsAFf7ogz9MvmsttgBwb.iXFap9hFfR6qX50VA/2y0NixXwh82we', '5551012020'),
  (2, 'Dr. Mark Johnson', 'Neurologist', 'mark.johnson@smartclinic.com', '$2b$10$ELEGEQ./0fqHHrid3HG.RelAErpKqxgkQJCkHXOi9IldITa2wjSge', '5552023030'),
  (3, 'Dr. Sarah Lee', 'Orthopedist', 'sarah.lee@smartclinic.com', '$2b$10$Gnyeh9GoZC0Aht2ZZAwQHeJDmnjvfy1MZLyOu4L8FrsaeOpCkw4oK', '5553034040'),
  (4, 'Dr. Tom Wilson', 'Pediatrician', 'tom.wilson@smartclinic.com', '$2b$10$v6upuZF0Glm7g3fsbCT9OOGFmWmRktmzT6d8xqPWInYMOgo.ZW0v6', '5554045050'),
  (5, 'Dr. Alice Brown', 'Dermatologist', 'alice.brown@smartclinic.com', '$2b$10$JvIcPEnXcU4HwOx5C9BY5.CrCdLP0JQodmTukwziSS5tvcMVNT4si', '5555056060'),
  (6, 'Dr. David Kim', 'Cardiologist', 'david.kim@smartclinic.com', '$2b$10$nQJFoVB6Mibg.IFXhwLHVuZ1HBzCXt/8T9dbCC1mUyrbJL1GIy9HG', '5556067070'),
  (7, 'Dr. Laura Martinez', 'Gynecologist', 'laura.martinez@smartclinic.com', '$2b$10$.HZsci49JCxcK8fYh2VDBeNYDBB7XkCBr4EiNZ8Wh/zbjp4b.Yptq', '5557078080'),
  (8, 'Dr. James Patel', 'General Physician', 'james.patel@smartclinic.com', '$2b$10$..qs6/U3GkcUYfpjANqZ.OWkINOtatXvft875DEaVOjtpTGz43aJS', '5558089090'),
  (9, 'Dr. Olivia Chen', 'Psychiatrist', 'olivia.chen@smartclinic.com', '$2b$10$BeyyfkIJn/qA1neM3fBwL.qd1qTxjtsKzGQcr2cI1f.lxseUIXpHe', '5559091010'),
  (10, 'Dr. Robert Garcia', 'ENT Specialist', 'robert.garcia@smartclinic.com', '$2b$10$fWpiDVyDJnyQpWFdl24OduXGwRZPYyLzk6oiew29jfN2F0bK2u6Ci', '5550102121');

INSERT INTO doctor_available_times (doctor_id, available_times) VALUES
  (1, '09:00-10:00'),
  (1, '10:00-11:00'),
  (1, '11:00-12:00'),
  (1, '14:00-15:00'),
  (2, '14:00-15:00'),
  (2, '15:00-16:00'),
  (2, '16:00-17:00'),
  (3, '09:00-10:00'),
  (3, '10:00-11:00'),
  (3, '11:00-12:00'),
  (4, '09:00-10:00'),
  (4, '10:00-11:00'),
  (4, '11:00-12:00'),
  (4, '14:00-15:00'),
  (4, '15:00-16:00'),
  (4, '16:00-17:00'),
  (5, '14:00-15:00'),
  (5, '15:00-16:00'),
  (6, '10:00-11:00'),
  (6, '11:00-12:00'),
  (6, '14:00-15:00'),
  (7, '09:00-10:00'),
  (7, '10:00-11:00'),
  (7, '11:00-12:00'),
  (8, '09:00-10:00'),
  (8, '10:00-11:00'),
  (8, '11:00-12:00'),
  (8, '14:00-15:00'),
  (8, '15:00-16:00'),
  (8, '16:00-17:00'),
  (9, '14:00-15:00'),
  (9, '15:00-16:00'),
  (9, '16:00-17:00'),
  (10, '09:00-10:00'),
  (10, '10:00-11:00'),
  (10, '15:00-16:00'),
  (10, '16:00-17:00');

INSERT INTO patient (id, name, email, password, phone, address) VALUES
  (1, 'John Smith', 'john.smith@example.com', '$2b$10$3lvgw3lGMlQ.iEkwGsctFODMQw4W0.tJaGfR911/vzl8OiJMvvjcO', '5551112222', '12 Oak Street, Springfield'),
  (2, 'Maria Gonzalez', 'maria.gonzalez@example.com', '$2b$10$.bHH9HLiuhjajCm4Tvf/A.CbRCDhlQqDBiAnWOLh23WyDbKArYqxC', '5552223333', '45 Pine Avenue, Riverside'),
  (3, 'Ahmed Khan', 'ahmed.khan@example.com', '$2b$10$MC0/3vYHLo.vta3TZhBhZ.WUbB0nVUHmmPWS1AWf4CAkQrrpFGhvC', '5553334444', '78 Maple Road, Lakeside'),
  (4, 'Priya Sharma', 'priya.sharma@example.com', '$2b$10$S5mVRVXrLIlqLyNBPslFpetqN4qK46.Dt27yqq4ubA94LXX3g2q5S', '5554445555', '9 Cedar Lane, Hillview'),
  (5, 'Liam O''Connor', 'liam.oconnor@example.com', '$2b$10$iizA3.QmD3PaTQscDFNvLO2Mgdya9y.6KVtXScdbY60SLouWf2PPi', '5555556666', '31 Birch Court, Greenfield'),
  (6, 'Sofia Rossi', 'sofia.rossi@example.com', '$2b$10$Lo2ShANdGZYlnZQju3f1MuYmGu2..qXOsX4wWcJIKfMe.KcQNcVHq', '5556667777', '64 Elm Street, Brookside'),
  (7, 'Chen Wei', 'chen.wei@example.com', '$2b$10$hMUpabAzBkPiOlTL7KjtLeveQz3WouJVb79bm7NoXyk2cZDa1N9YK', '5557778888', '22 Willow Way, Fairview'),
  (8, 'Fatima Noor', 'fatima.noor@example.com', '$2b$10$RujIhkKBTChjcv37dSeJ3eWL7u4M0HRBSNHzrXqFMXqdkDuQJgr0q', '5558889999', '5 Spruce Drive, Westwood'),
  (9, 'Noah Williams', 'noah.williams@example.com', '$2b$10$QLuVNEKNFaxYk0vgyoS9oOJjv.3m7GWnnlTuZBXuS197IMitJl1bW', '5559990000', '88 Aspen Place, Eastbrook'),
  (10, 'Emma Davis', 'emma.davis@example.com', '$2b$10$pLR5DX1RiLJCoF5bEu1Cu.no1EUlxN41E7JnRZNqjuIc1iZ6./nG.', '5550001111', '17 Poplar Street, Northgate');

-- status: 1 = completed (past appointments), 0 = scheduled (today and later)
INSERT INTO appointment (id, doctor_id, patient_id, appointment_time, status) VALUES
  (1, 1, 1, TIMESTAMP(CURDATE() + INTERVAL -58 DAY, '09:00:00'), 1),
  (2, 2, 2, TIMESTAMP(CURDATE() + INTERVAL -55 DAY, '14:00:00'), 1),
  (3, 3, 3, TIMESTAMP(CURDATE() + INTERVAL -52 DAY, '10:00:00'), 1),
  (4, 4, 4, TIMESTAMP(CURDATE() + INTERVAL -50 DAY, '11:00:00'), 1),
  (5, 1, 5, TIMESTAMP(CURDATE() + INTERVAL -47 DAY, '10:00:00'), 1),
  (6, 5, 6, TIMESTAMP(CURDATE() + INTERVAL -44 DAY, '15:00:00'), 1),
  (7, 8, 7, TIMESTAMP(CURDATE() + INTERVAL -41 DAY, '09:00:00'), 1),
  (8, 6, 8, TIMESTAMP(CURDATE() + INTERVAL -38 DAY, '10:00:00'), 1),
  (9, 7, 9, TIMESTAMP(CURDATE() + INTERVAL -35 DAY, '11:00:00'), 1),
  (10, 9, 10, TIMESTAMP(CURDATE() + INTERVAL -33 DAY, '16:00:00'), 1),
  (11, 1, 2, TIMESTAMP(CURDATE() + INTERVAL -30 DAY, '11:00:00'), 1),
  (12, 10, 1, TIMESTAMP(CURDATE() + INTERVAL -27 DAY, '15:00:00'), 1),
  (13, 4, 3, TIMESTAMP(CURDATE() + INTERVAL -24 DAY, '14:00:00'), 1),
  (14, 8, 4, TIMESTAMP(CURDATE() + INTERVAL -21 DAY, '16:00:00'), 1),
  (15, 2, 5, TIMESTAMP(CURDATE() + INTERVAL -18 DAY, '15:00:00'), 1),
  (16, 3, 6, TIMESTAMP(CURDATE() + INTERVAL -15 DAY, '09:00:00'), 1),
  (17, 6, 7, TIMESTAMP(CURDATE() + INTERVAL -12 DAY, '14:00:00'), 1),
  (18, 1, 8, TIMESTAMP(CURDATE() + INTERVAL -9 DAY, '09:00:00'), 1),
  (19, 5, 9, TIMESTAMP(CURDATE() + INTERVAL -6 DAY, '14:00:00'), 1),
  (20, 8, 10, TIMESTAMP(CURDATE() + INTERVAL -3 DAY, '10:00:00'), 1),
  (21, 1, 3, TIMESTAMP(CURDATE() + INTERVAL 0 DAY, '14:00:00'), 0),
  (22, 4, 5, TIMESTAMP(CURDATE() + INTERVAL 0 DAY, '15:00:00'), 0),
  (23, 8, 9, TIMESTAMP(CURDATE() + INTERVAL 0 DAY, '16:00:00'), 0),
  (24, 1, 4, TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '10:00:00'), 0),
  (25, 2, 6, TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '16:00:00'), 0),
  (26, 3, 7, TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '11:00:00'), 0),
  (27, 7, 8, TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '09:00:00'), 0),
  (28, 9, 1, TIMESTAMP(CURDATE() + INTERVAL 8 DAY, '14:00:00'), 0),
  (29, 10, 2, TIMESTAMP(CURDATE() + INTERVAL 10 DAY, '09:00:00'), 0),
  (30, 6, 3, TIMESTAMP(CURDATE() + INTERVAL 12 DAY, '11:00:00'), 0),
  (31, 4, 10, TIMESTAMP(CURDATE() + INTERVAL 15 DAY, '09:00:00'), 0),
  (32, 5, 4, TIMESTAMP(CURDATE() + INTERVAL 18 DAY, '15:00:00'), 0),
  (33, 8, 6, TIMESTAMP(CURDATE() + INTERVAL 21 DAY, '11:00:00'), 0);

COMMIT;
