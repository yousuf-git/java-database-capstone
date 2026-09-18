// doctorDashboard.js
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");

let selectedDate = new Date().toISOString().split("T")[0];
// The backend expects the literal string "null" when no name filter is set.
let patientName = "null";

document.addEventListener("DOMContentLoaded", () => {
  const datePicker = document.getElementById("datePicker");
  datePicker.value = selectedDate;

  document.getElementById("searchBar").addEventListener("input", (event) => {
    const value = event.target.value.trim();
    patientName = value.length > 0 ? value : "null";
    loadAppointments();
  });

  document.getElementById("todayButton").addEventListener("click", () => {
    selectedDate = new Date().toISOString().split("T")[0];
    datePicker.value = selectedDate;
    loadAppointments();
  });

  datePicker.addEventListener("change", (event) => {
    selectedDate = event.target.value;
    loadAppointments();
  });

  loadAppointments();
});

async function loadAppointments() {
  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    const appointments = data.appointments ?? [];
    tableBody.innerHTML = "";

    if (appointments.length === 0) {
      tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan="5">No appointments found for ${selectedDate}.</td></tr>`;
      return;
    }

    appointments.forEach(appointment => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail
      };
      tableBody.appendChild(createPatientRow(patient, appointment.id, appointment.doctorId));
    });
  } catch (error) {
    console.error("Failed to load appointments:", error);
    tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan="5">Error loading appointments. Try again later.</td></tr>`;
  }
}
