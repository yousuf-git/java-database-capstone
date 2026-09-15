# Smart Clinic Management System – Schema Design

The Smart Clinic stores its data in two databases, chosen by the shape of the data:

- **MySQL (database `cms`)** holds the core, structured records: admins, doctors, patients, and appointments. These records have a fixed set of fields, are linked to each other (an appointment always belongs to one doctor and one patient), and need strong guarantees such as unique emails and valid foreign keys. A relational database with constraints and transactions fits this best.
- **MongoDB (database `prescriptions`)** holds prescriptions. A prescription's content varies from case to case (one medicine or several, optional refills, pharmacy details, free-text notes), and new fields can be added over time without a schema migration. A document database fits this best.

The two databases are linked by ID: each prescription document stores the `appointmentId` of the MySQL appointment it was written for.

## MySQL Database Design

Table and column names follow the naming Spring Boot and Hibernate generate from the JPA entities (for example, the `Doctor` entity maps to the `doctor` table and the `availableTimes` field maps to `available_times`), so the design matches the tables the application creates.

### Table: admin

Stores the accounts of clinic administrators, who manage doctors and the portal.

| Column   | Data Type    | Constraints                  | Notes                              |
|----------|--------------|------------------------------|------------------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT  | Surrogate key                      |
| username | VARCHAR(50)  | NOT NULL, UNIQUE             | Login name                         |
| password | VARCHAR(255) | NOT NULL                     | Password hash (BCrypt), never plain text |

```sql
CREATE TABLE admin (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(255) NOT NULL,          -- 255 leaves room for any hash format
    PRIMARY KEY (id),
    CONSTRAINT uq_admin_username UNIQUE (username)   -- two admins cannot share a login
);
```

### Table: doctor

Stores doctor profiles. Doctors are created by an admin and log in with their email.

| Column    | Data Type    | Constraints                          | Notes                               |
|-----------|--------------|--------------------------------------|-------------------------------------|
| id        | BIGINT       | PRIMARY KEY, AUTO_INCREMENT          | Surrogate key                       |
| name      | VARCHAR(100) | NOT NULL                             | 3–100 characters (validated in app) |
| specialty | VARCHAR(50)  | NOT NULL                             | For example, Cardiologist           |
| email     | VARCHAR(100) | NOT NULL, UNIQUE                     | Login identifier                    |
| password  | VARCHAR(255) | NOT NULL                             | Password hash                       |
| phone     | VARCHAR(10)  | NOT NULL, CHECK exactly 10 digits    | Stored as text to keep leading zeros |

```sql
CREATE TABLE doctor (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    specialty VARCHAR(50)  NOT NULL,
    email     VARCHAR(100) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    phone     VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_doctor_email UNIQUE (email),                     -- email is the login name
    CONSTRAINT chk_doctor_phone CHECK (phone REGEXP '^[0-9]{10}$'),
    INDEX idx_doctor_specialty (specialty)                          -- patients filter doctors by specialty
);
```

### Table: doctor_available_times

Stores each doctor's bookable time slots, one row per slot. This is the table JPA creates for the `@ElementCollection` field `Doctor.availableTimes`.

| Column          | Data Type   | Constraints                                   | Notes                            |
|-----------------|-------------|-----------------------------------------------|----------------------------------|
| doctor_id       | BIGINT      | NOT NULL, FOREIGN KEY → doctor(id)            | Owning doctor                    |
| available_times | VARCHAR(20) | NOT NULL                                      | Slot in `HH:MM-HH:MM` format     |

```sql
CREATE TABLE doctor_available_times (
    doctor_id       BIGINT      NOT NULL,
    available_times VARCHAR(20) NOT NULL,    -- for example '09:00-10:00'
    PRIMARY KEY (doctor_id, available_times), -- the same slot cannot be listed twice for one doctor
    CONSTRAINT fk_available_times_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctor (id) ON DELETE CASCADE  -- slots mean nothing without their doctor
);
```

A separate table is used instead of a comma-separated column so that each slot can be queried, added, and removed on its own.

### Table: patient

Stores patient accounts. Patients register themselves and log in with their email.

| Column   | Data Type    | Constraints                       | Notes                                |
|----------|--------------|-----------------------------------|--------------------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT       | Surrogate key                        |
| name     | VARCHAR(100) | NOT NULL                          | 3–100 characters (validated in app)  |
| email    | VARCHAR(100) | NOT NULL, UNIQUE                  | Login identifier                     |
| password | VARCHAR(255) | NOT NULL                          | Password hash                        |
| phone    | VARCHAR(10)  | NOT NULL, CHECK exactly 10 digits | Contact number                       |
| address  | VARCHAR(255) | NOT NULL                          | Home address                         |

```sql
CREATE TABLE patient (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone    VARCHAR(10)  NOT NULL,
    address  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_patient_email UNIQUE (email),                     -- one account per email
    CONSTRAINT chk_patient_phone CHECK (phone REGEXP '^[0-9]{10}$')
);
```

### Table: appointment

Stores bookings. Each appointment links one doctor and one patient to a one-hour slot.

| Column           | Data Type   | Constraints                         | Notes                                   |
|------------------|-------------|-------------------------------------|-----------------------------------------|
| id               | BIGINT      | PRIMARY KEY, AUTO_INCREMENT         | Surrogate key                           |
| doctor_id        | BIGINT      | NOT NULL, FOREIGN KEY → doctor(id)  | Doctor being consulted                  |
| patient_id       | BIGINT      | NOT NULL, FOREIGN KEY → patient(id) | Patient who booked                      |
| appointment_time | DATETIME    | NOT NULL                            | Start time; end time = start + 1 hour   |
| status           | INT         | NOT NULL, DEFAULT 0, CHECK IN (0, 1) | 0 = scheduled, 1 = completed           |

```sql
CREATE TABLE appointment (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    doctor_id        BIGINT   NOT NULL,
    patient_id       BIGINT   NOT NULL,
    appointment_time DATETIME NOT NULL,
    status           INT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT uq_appointment_doctor_time UNIQUE (doctor_id, appointment_time),  -- blocks double-booking a doctor's slot
    CONSTRAINT chk_appointment_status CHECK (status IN (0, 1)),
    INDEX idx_appointment_patient (patient_id, appointment_time)                  -- "my upcoming appointments" lookups
);
```

Design decisions for `appointment`:

- The foreign keys have no `ON DELETE CASCADE`. Deleting a doctor or patient that still has appointments fails at the database level, so appointment history is never removed by accident. When an admin deletes a doctor, the service layer deletes that doctor's appointments first, on purpose.
- The unique key on `(doctor_id, appointment_time)` also serves as the index for "a doctor's appointments on a given day", so no separate index is needed for that query.
- The end time is not stored. Every appointment lasts one hour, so the application calculates it from `appointment_time`.
- The rule that an appointment must be in the future is checked by the application (`@Future`), not by the database, because a `CHECK` constraint in MySQL cannot use the current time.

### Relationships

- One **doctor** has many **appointments**; one **patient** has many **appointments** (both one-to-many, through `appointment.doctor_id` and `appointment.patient_id`).
- One **doctor** has many **available time slots** (one-to-many, through `doctor_available_times.doctor_id`).
- One **appointment** can have prescriptions in MongoDB, linked by `appointmentId` (no database-level foreign key across the two databases).
- **admin** has no relationships; it only controls access to the admin features.

## MongoDB Collection Design

### Collection: prescriptions

Each document is one prescription written by a doctor during an appointment.

```json
{
  "_id": { "$oid": "6512bd43d9caa6e02c990b0a" },
  "patientName": "Jane Doe",
  "appointmentId": 51,
  "medication": "Amoxicillin",
  "dosage": "500 mg, 3 times a day for 7 days",
  "doctorNotes": "Take after meals. Return if the fever lasts more than 3 days.",
  "refillCount": 1,
  "pharmacy": {
    "name": "City Care Pharmacy",
    "location": "12 Market Street"
  },
  "tags": ["antibiotic", "infection"],
  "createdAt": { "$date": "2026-09-15T10:30:00Z" }
}
```

| Field         | Type     | Required | Notes                                                        |
|---------------|----------|----------|--------------------------------------------------------------|
| _id           | ObjectId | Yes      | Generated by MongoDB                                         |
| patientName   | String   | Yes      | 3–100 characters                                             |
| appointmentId | Number   | Yes      | ID of the MySQL `appointment` row                            |
| medication    | String   | Yes      | 3–100 characters                                             |
| dosage        | String   | Yes      | Amount, frequency, and duration                              |
| doctorNotes   | String   | No       | Up to 200 characters                                         |
| refillCount   | Number   | No       | Number of refills allowed                                    |
| pharmacy      | Object   | No       | Nested `name` and `location` of the preferred pharmacy       |
| tags          | Array    | No       | Free labels used for searching and grouping prescriptions    |
| createdAt     | Date     | Yes      | When the prescription was written                            |

Design decisions for `prescriptions`:

- **Reference the appointment by ID instead of copying it.** Only `appointmentId` is stored, so the appointment's doctor, patient, and time stay in one place (MySQL) and never go out of sync. The application loads the appointment from MySQL when it needs those details.
- **Copy the patient's name.** `patientName` is duplicated on purpose so that a prescription can be displayed or printed without a second query to MySQL. A prescription is a record of what was written at that time, so it should keep the name as it was.
- **Nest data that belongs only to this prescription.** Pharmacy details are embedded as a sub-document because they are only ever read together with the prescription and are not shared with other records.
- **Keep optional fields optional.** `refillCount`, `pharmacy`, and `tags` are simply left out when they do not apply, which is where MongoDB's flexible schema helps. New fields (for example, a list of several medicines) can be added later without changing existing documents.
- **Index `appointmentId`.** The most common query is "the prescriptions for this appointment", so an index on `appointmentId` keeps it fast:

  ```js
  db.prescriptions.createIndex({ appointmentId: 1 })
  ```
