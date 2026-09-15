-- Reporting stored procedures for the Smart Clinic `cms` MySQL database.
--
-- Load with:
--   mysql -u <user> -p cms < database/stored-procedures.sql
--
-- Each procedure is dropped first, so the file can be re-run after changes.

DELIMITER $$

-- Every appointment on the given day, grouped by doctor, with the patient's contact details.
DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor $$
CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT d.name                                   AS doctor_name,
           TIME_FORMAT(a.appointment_time, '%H:%i')  AS appointment_time,
           CASE a.status WHEN 0 THEN 'Scheduled' WHEN 1 THEN 'Completed' END AS status,
           p.name                                   AS patient_name,
           p.phone                                  AS patient_phone
    FROM appointment a
    JOIN doctor d  ON a.doctor_id = d.id
    JOIN patient p ON a.patient_id = p.id
    WHERE DATE(a.appointment_time) = report_date
    ORDER BY d.name, a.appointment_time;
END $$

-- The doctor who saw the most different patients in the given month.
-- COUNT(DISTINCT ...) so a patient with several appointments counts once; ties go to the lower doctor ID.
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth $$
CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN input_month INT, IN input_year INT)
BEGIN
    SELECT a.doctor_id,
           d.name                      AS doctor_name,
           COUNT(DISTINCT a.patient_id) AS patients_seen
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE MONTH(a.appointment_time) = input_month
      AND YEAR(a.appointment_time) = input_year
    GROUP BY a.doctor_id, d.name
    ORDER BY patients_seen DESC, a.doctor_id
    LIMIT 1;
END $$

-- The doctor who saw the most different patients in the given year.
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear $$
CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN input_year INT)
BEGIN
    SELECT a.doctor_id,
           d.name                      AS doctor_name,
           COUNT(DISTINCT a.patient_id) AS patients_seen
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time) = input_year
    GROUP BY a.doctor_id, d.name
    ORDER BY patients_seen DESC, a.doctor_id
    LIMIT 1;
END $$

DELIMITER ;
