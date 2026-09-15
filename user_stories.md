# Smart Clinic Management System – User Stories

Each story follows this template:

```md
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]
```

## Admin User Stories

### Admin: Log in to the portal

**Title:**
_As an admin, I want to log in to the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. The login page accepts a username and password.
2. Valid credentials return a JWT and redirect the admin to the Admin Dashboard.
3. Invalid credentials show an error message and do not issue a token.

**Priority:** High
**Story Points:** 3
**Notes:**
- Passwords must never be stored or logged in plain text.

### Admin: Log out of the portal

**Title:**
_As an admin, I want to log out of the portal, so that nobody else can use my session to access the system._

**Acceptance Criteria:**
1. A logout button is visible on every admin page.
2. Logging out removes the stored token and returns the admin to the login page.
3. Protected admin pages cannot be opened after logout without logging in again.

**Priority:** High
**Story Points:** 1
**Notes:**
- The token is removed on the client side; the backend rejects requests without a valid token.

### Admin: Add a doctor

**Title:**
_As an admin, I want to add a doctor to the portal, so that patients can find and book appointments with that doctor._

**Acceptance Criteria:**
1. The admin can open an "Add Doctor" form with name, specialty, email, phone, password, and available times.
2. Required fields and formats (email, 10-digit phone) are validated before saving.
3. The new doctor appears in the doctor list immediately after saving.

**Priority:** High
**Story Points:** 3
**Notes:**
- Adding a doctor with an email that already exists must be rejected with a clear message.

### Admin: Delete a doctor

**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that doctors who have left the clinic no longer appear to patients._

**Acceptance Criteria:**
1. Each doctor card on the Admin Dashboard has a delete action.
2. The admin must confirm before the doctor is deleted.
3. The doctor is removed from the list and can no longer be booked.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Decide how existing appointments for the deleted doctor are handled (for example, delete them along with the doctor).

### Admin: View monthly appointment statistics

**Title:**
_As an admin, I want to run a stored procedure in the MySQL CLI that returns the number of appointments per month, so that I can track how much the clinic is being used._

**Acceptance Criteria:**
1. A stored procedure exists in the MySQL database that groups appointments by month.
2. Running it from the MySQL CLI returns the month and the appointment count for each month.
3. The result matches the actual appointment data in the database.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- This is a reporting feature; it is run from the database, not the web UI.

### Admin: Search and filter doctors

**Title:**
_As an admin, I want to search doctors by name and filter them by specialty and available time, so that I can quickly find a specific doctor's record._

**Acceptance Criteria:**
1. The Admin Dashboard has a search box and filters for specialty and time (AM/PM).
2. The doctor list updates to show only doctors matching the search and filters.
3. A "No doctors found" message appears when nothing matches.

**Priority:** Low
**Story Points:** 2
**Notes:**
- Search should be case-insensitive and match partial names.

## Patient User Stories

### Patient: Browse doctors without logging in

**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._

**Acceptance Criteria:**
1. The public page lists doctors with their name, specialty, and contact details.
2. The patient can search and filter the list without an account.
3. Trying to book an appointment prompts the patient to log in or sign up.

**Priority:** High
**Story Points:** 2
**Notes:**
- Only non-sensitive doctor information is shown publicly.

### Patient: Sign up

**Title:**
_As a patient, I want to sign up with my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. The sign-up form collects name, email, password, phone, and address.
2. All fields are validated, and an email that is already registered is rejected.
3. After a successful sign-up, the patient can log in with the new credentials.

**Priority:** High
**Story Points:** 3
**Notes:**
- Passwords must meet a minimum length.

### Patient: Log in to the portal

**Title:**
_As a patient, I want to log in to the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. The patient logs in with email and password.
2. Valid credentials return a JWT and take the patient to their dashboard.
3. Invalid credentials show an error message.

**Priority:** High
**Story Points:** 2
**Notes:**
- The token must be sent with every protected API request.

### Patient: Log out of the portal

**Title:**
_As a patient, I want to log out of the portal, so that my account and health information stay secure._

**Acceptance Criteria:**
1. A logout option is available on every logged-in patient page.
2. Logging out removes the stored token and returns the patient to the home page.
3. Booking and appointment pages are not accessible after logout.

**Priority:** High
**Story Points:** 1
**Notes:**
- Especially important on shared or public computers.

### Patient: Book an appointment

**Title:**
_As a patient, I want to book an hour-long appointment with a doctor, so that I can consult with them about my health._

**Acceptance Criteria:**
1. The patient chooses a doctor, a date, and one of the doctor's available time slots.
2. Slots that are already booked or marked unavailable cannot be selected.
3. After booking, the patient sees a confirmation, and the appointment appears in their list.

**Priority:** High
**Story Points:** 5
**Notes:**
- Two patients must not be able to book the same doctor at the same time.

### Patient: View upcoming appointments

**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare for them._

**Acceptance Criteria:**
1. The patient sees a list of their upcoming appointments with the doctor's name, date, and time.
2. Past and upcoming appointments can be filtered separately.
3. A patient can only see their own appointments.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Appointments are sorted by date, soonest first.

## Doctor User Stories

### Doctor: Log in to the portal

**Title:**
_As a doctor, I want to log in to the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. The doctor logs in with email and password.
2. Valid credentials return a JWT and open the Doctor Dashboard.
3. Invalid credentials show an error message.

**Priority:** High
**Story Points:** 2
**Notes:**
- Doctor accounts are created by the admin, not through public sign-up.

### Doctor: Log out of the portal

**Title:**
_As a doctor, I want to log out of the portal, so that my patients' data stays protected._

**Acceptance Criteria:**
1. A logout option is available on every doctor page.
2. Logging out removes the stored token and returns the doctor to the login page.
3. The Doctor Dashboard cannot be opened after logout without logging in again.

**Priority:** High
**Story Points:** 1
**Notes:**
- None.

### Doctor: View appointment calendar

**Title:**
_As a doctor, I want to view my appointments for a selected day, so that I can stay organized._

**Acceptance Criteria:**
1. The Doctor Dashboard shows today's appointments by default.
2. The doctor can pick another date to see that day's appointments.
3. Each appointment shows the patient's name, time, and contact details.

**Priority:** High
**Story Points:** 3
**Notes:**
- A doctor only sees their own appointments.

### Doctor: Mark unavailability

**Title:**
_As a doctor, I want to mark the times I am unavailable, so that patients can only book the slots I am actually free._

**Acceptance Criteria:**
1. The doctor can add and remove available time slots in their profile.
2. Slots that are not available are not offered to patients during booking.
3. Changes take effect for new bookings immediately.

**Priority:** High
**Story Points:** 3
**Notes:**
- Existing appointments in a slot that becomes unavailable are not cancelled automatically.

### Doctor: Update profile

**Title:**
_As a doctor, I want to update my profile with my specialization and contact information, so that patients have up-to-date information about me._

**Acceptance Criteria:**
1. The doctor can edit their specialty, phone number, and email.
2. Updated fields are validated before saving.
3. The changes are visible on the public doctor list after saving.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Changing the email must not collide with another doctor's email.

### Doctor: View patient details and add prescriptions

**Title:**
_As a doctor, I want to view patient details for my upcoming appointments and add prescriptions, so that I am prepared for each visit and my patients get the right treatment._

**Acceptance Criteria:**
1. The doctor can open a patient's details from an appointment in their dashboard.
2. The doctor can add a prescription (medication, dosage, and notes) to that appointment.
3. The prescription is saved and linked to the patient and the appointment.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Prescriptions are stored in MongoDB because their structure is flexible.
