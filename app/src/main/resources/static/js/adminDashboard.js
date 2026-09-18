// adminDashboard.js
import { openModal } from "./components/modals.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";

document.addEventListener("DOMContentLoaded", () => {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

  loadDoctorCards();
});

async function loadDoctorCards() {
  try {
    renderDoctorCards(await getDoctors());
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}

async function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;

  const name = searchBar.length > 0 ? searchBar : null;
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  try {
    const { doctors } = await filterDoctors(name, time, specialty);
    if (doctors.length === 0) {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
      return;
    }
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("An error occurred while filtering doctors.");
  }
}

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach(doctor => contentDiv.appendChild(createDoctorCard(doctor)));
}

window.adminAddDoctor = async function () {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please log in again.");
    return;
  }

  const doctor = {
    name: document.getElementById("doctorName").value,
    specialty: document.getElementById("specialization").value,
    email: document.getElementById("doctorEmail").value,
    password: document.getElementById("doctorPassword").value,
    phone: document.getElementById("doctorPhone").value,
    availableTimes: Array.from(document.querySelectorAll("input[name='availability']:checked"))
      .map(checkbox => checkbox.value)
  };

  const { success, message } = await saveDoctor(doctor, token);
  alert(message || (success ? "Doctor added" : "Failed to add doctor"));
  if (success) {
    document.getElementById("modal").style.display = "none";
    loadDoctorCards();
  }
};
