// doctorCard.js
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const role = localStorage.getItem("userRole");

  const info = document.createElement("div");
  info.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialty = document.createElement("p");
  specialty.textContent = `Specialty: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  const times = doctor.availableTimes ?? [];
  availability.textContent = `Available: ${times.length > 0 ? times.join(", ") : "No slots listed"}`;

  info.append(name, specialty, email, availability);

  const actions = document.createElement("div");
  actions.classList.add("card-actions");

  if (role === "admin") {
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.addEventListener("click", async () => {
      if (!confirm(`Delete ${doctor.name}? This also removes their appointments.`)) {
        return;
      }
      const token = localStorage.getItem("token");
      const { success, message } = await deleteDoctor(doctor.id, token);
      alert(message || (success ? "Doctor deleted." : "Failed to delete doctor."));
      if (success) {
        card.remove();
      }
    });
    actions.appendChild(deleteBtn);
  } else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });
    actions.appendChild(bookBtn);
  } else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        localStorage.setItem("userRole", "patient");
        window.location.href = "/pages/patientDashboard.html";
        return;
      }
      const patient = await getPatientData(token);
      if (!patient) {
        alert("Could not load your details. Please log in again.");
        return;
      }
      // Imported on demand: loggedPatient.js wires up page listeners when it loads, which must not
      // happen on the admin and public dashboards that also render doctor cards.
      const { showBookingOverlay } = await import("../loggedPatient.js");
      showBookingOverlay(e, doctor, patient);
    });
    actions.appendChild(bookBtn);
  }

  card.appendChild(info);
  if (actions.childElementCount > 0) {
    card.appendChild(actions);
  }
  return card;
}
