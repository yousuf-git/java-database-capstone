# Stored Procedure Output

Output of each reporting procedure in `database/stored-procedures.sql`, run in the MySQL CLI against the sample data from `database/sample-data.sql` on 2026-09-15. The sample appointment dates are relative to the day the data was loaded, so the results differ if the data is loaded on another day.

## Daily appointment report by doctor

```text
mysql> CALL GetDailyAppointmentReportByDoctor('2026-09-15');
+-----------------+------------------+-----------+---------------+---------------+
| doctor_name     | appointment_time | status    | patient_name  | patient_phone |
+-----------------+------------------+-----------+---------------+---------------+
| Dr. Emily Adams | 14:00            | Scheduled | Ahmed Khan    | 5553334444    |
| Dr. James Patel | 16:00            | Scheduled | Noah Williams | 5559990000    |
| Dr. Tom Wilson  | 15:00            | Scheduled | Liam O'Connor | 5555556666    |
+-----------------+------------------+-----------+---------------+---------------+
```

## Doctor with the most patients in a month

```text
mysql> CALL GetDoctorWithMostPatientsByMonth(8, 2026);
+-----------+-----------------+---------------+
| doctor_id | doctor_name     | patients_seen |
+-----------+-----------------+---------------+
|         8 | Dr. James Patel |             2 |
+-----------+-----------------+---------------+
```

## Doctor with the most patients in a year

```text
mysql> CALL GetDoctorWithMostPatientsByYear(2026);
+-----------+-----------------+---------------+
| doctor_id | doctor_name     | patients_seen |
+-----------+-----------------+---------------+
|         1 | Dr. Emily Adams |             6 |
+-----------+-----------------+---------------+
```
