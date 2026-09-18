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
-- Phone numbers are Pakistani mobile numbers without the leading zero, because the column
-- holds exactly 10 digits (for example 0300 1234567 is stored as 3001234567).
--
-- Appointment dates are relative to the day the script is run, so there are always past
-- (completed), today's, and upcoming (scheduled) appointments to work with.
-- Rows use fixed IDs so the MongoDB prescriptions can reference appointments by ID; running the
-- script twice stops at the first duplicate ID and the transaction is rolled back.

START TRANSACTION;

INSERT INTO admin (id, username, password) VALUES
  (1, 'admin', '$2b$10$XIYoGuWiOb.xMwqP7r/UwuFsjBNARRWbQsN/GG23jB4QfnBinnnN2'),
  (2, 'clinic_manager', '$2b$10$w7Pv3OkDf24boYy.CiZ0CO7ll780AYnhAI0fM5w87TEQa0ZfKsZaS');

INSERT INTO doctor (id, name, specialty, email, password, phone) VALUES
  (1, 'Dr. Ayesha Khan', 'Cardiologist', 'ayesha.khan@smartclinic.pk', '$2b$10$8g9vtlLPAQ.FsNu4j7vsouJRFO0e75TgEMJttOyUjFa8/avYvSTY.', '3001234567'),
  (2, 'Dr. Bilal Ahmed', 'Neurologist', 'bilal.ahmed@smartclinic.pk', '$2b$10$Lb5Z8kFPSRq36TkV53zSRuqaaXOD44c.n141sqpdoiXi0q1C88gDi', '3211234568'),
  (3, 'Dr. Sana Malik', 'Orthopedist', 'sana.malik@smartclinic.pk', '$2b$10$f55QGERVH/CUjhrmtFnDOuCc0dsEDVag/bZ9mNXaec3yqCa/Q25ja', '3331234569'),
  (4, 'Dr. Usman Tariq', 'Pediatrician', 'usman.tariq@smartclinic.pk', '$2b$10$IwK2r4M52RdGnGqGTe.XWeDmmCbraFvMv4B6B4Hlj/DL5kzeh/Fl2', '3451234570'),
  (5, 'Dr. Hina Shahid', 'Dermatologist', 'hina.shahid@smartclinic.pk', '$2b$10$gOg2M/3jNMkWTHQPGOqCsOGI1zxnOvgrEdIXivdAesbul4Qp956GK', '3011234571'),
  (6, 'Dr. Imran Qureshi', 'Cardiologist', 'imran.qureshi@smartclinic.pk', '$2b$10$40kIWmtA9LNEmhUc7FT3t.YUQ4CZXgAIKSM4Tex/A54Gsp8DchEMm', '3221234572'),
  (7, 'Dr. Nadia Aslam', 'Gynecologist', 'nadia.aslam@smartclinic.pk', '$2b$10$hSCUz9TfoytNu9Nix6lhQ.nk.eZteruNdJDxrCkXyRlNl/2PDmBLi', '3341234573'),
  (8, 'Dr. Faisal Raza', 'General Physician', 'faisal.raza@smartclinic.pk', '$2b$10$Hhn9dgrxx/4JD2Ah5cupC.uK9DMLO6fPj9PPfqeaqhGFNC5/asHUS', '3461234574'),
  (9, 'Dr. Zainab Iqbal', 'Psychiatrist', 'zainab.iqbal@smartclinic.pk', '$2b$10$HgnsKcAEmGDkRj3vESeGt.ATpfrhQuPp4JW1mvPo5tHbBKF0LGpmm', '3021234575'),
  (10, 'Dr. Kashif Mehmood', 'ENT Specialist', 'kashif.mehmood@smartclinic.pk', '$2b$10$CWLSZBO0BFRz.WltwVlvSuiSp7WSWirhjldOaPNkrADCcSIITyBxS', '3231234576');

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
  (1, 'Ali Hassan', 'ali.hassan@gmail.com', '$2b$10$dlRK1BYZlC6pjCUEitpdKu1BN8vjwoBe8.7.E112oC5e78n0MWZVO', '3009876543', 'House 12, Street 4, Gulshan-e-Iqbal, Karachi'),
  (2, 'Fatima Noor', 'fatima.noor@gmail.com', '$2b$10$YIJz1Z75FojgeQuz9pUF1uGgSOy0gElfp1mS.pE.ISUcMSpKHTDW2', '3218765432', 'Flat 5B, Askari Heights, DHA Phase 5, Lahore'),
  (3, 'Hamza Sheikh', 'hamza.sheikh@gmail.com', '$2b$10$ODvuQS7mJmPg.T5aSLfbEeg4uBUWINNGEghqqydw.uagWWKf.OSou', '3337654321', 'House 88, Sector G-11/3, Islamabad'),
  (4, 'Ayesha Siddiqui', 'ayesha.siddiqui@gmail.com', '$2b$10$NN2cCrBK.YqjhqiVCqqTk.A7zNonciqRDeuuStNJ1Zj4gRDhje.U6', '3456543210', 'House 23, Saddar Cantt, Rawalpindi'),
  (5, 'Bilal Javed', 'bilal.javed@gmail.com', '$2b$10$3votsnye5U1ddbJmZS1AV.yKybVV/QuOkzDRP75IqlqEwYkmBwvia', '3015432109', 'Plot 7, Model Town Block C, Lahore'),
  (6, 'Mariam Zafar', 'mariam.zafar@gmail.com', '$2b$10$kJvT5fhz673dM7WrrxRR8OpqI6KIjn/z9yENmtZPD/pOVcbIWmMKy', '3224321098', 'House 45, University Road, Peshawar'),
  (7, 'Saad Rehman', 'saad.rehman@gmail.com', '$2b$10$bCfNMqwpmho.WjDLxQpUC.ul5m698JUBezzfHwfXCrNrFeGbB9jyW', '3343210987', 'House 9, Satellite Town, Multan'),
  (8, 'Hira Aslam', 'hira.aslam@gmail.com', '$2b$10$ES2rgqCYCL151xbZCzDC9edHYjV01uVvWLg2AmgnPPD1m1YYjTjQG', '3462109876', 'Flat 302, Clifton Block 2, Karachi'),
  (9, 'Ahmed Raza', 'ahmed.raza@gmail.com', '$2b$10$4pMZFsYdPvISNJd4QhO6wuLItFEqzperNxHDJtz.KkXfyIpWGC.CO', '3021098765', 'House 61, Johar Town, Lahore'),
  (10, 'Sadia Khan', 'sadia.khan@gmail.com', '$2b$10$imw8b1pAHIO5X6k.CFafB.rYlszMRCtq3/qHnAMlviIqiV1Vrf68u', '3230987654', 'House 14, Wapda Town, Faisalabad');

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
