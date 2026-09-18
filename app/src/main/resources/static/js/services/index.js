// index.js
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_LOGIN_API = API_BASE_URL + "/doctor/login";

window.onload = function () {
  const adminLoginBtn = document.getElementById("adminLogin");
  if (adminLoginBtn) {
    adminLoginBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  const doctorLoginBtn = document.getElementById("doctorLogin");
  if (doctorLoginBtn) {
    doctorLoginBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

window.adminLoginHandler = async function () {
  const admin = {
    username: document.getElementById("username").value,
    password: document.getElementById("password").value
  };
  await login(ADMIN_API, admin, "admin");
};

window.doctorLoginHandler = async function () {
  const doctor = {
    email: document.getElementById("email").value,
    password: document.getElementById("password").value
  };
  await login(DOCTOR_LOGIN_API, doctor, "doctor");
};

async function login(url, credentials, role) {
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials)
    });
    const result = await response.json();
    if (!response.ok) {
      alert(result.message || "Invalid credentials!");
      return;
    }
    localStorage.setItem("token", result.token);
    selectRole(role);
  } catch (error) {
    console.error(`Error :: ${role} login ::`, error);
    alert("Something went wrong while logging in. Please try again.");
  }
}
