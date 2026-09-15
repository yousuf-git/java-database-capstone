// header.js

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) {
    return;
  }

  const logo = `
    <a href="/" class="logo-link">
      <img src="/assets/images/logo/logo.png" alt="Smart Clinic logo" class="logo-img">
      <span class="logo-title">Smart Clinic</span>
    </a>`;

  if (window.location.pathname === "/" || window.location.pathname.endsWith("/index.html")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `<header class="header">${logo}</header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let nav = "";
  if (role === "admin") {
    nav = `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "doctor") {
    nav = `
      <button id="doctorHomeBtn" class="adminBtn">Home</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "patient") {
    nav = `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    nav = `
      <button id="home" class="adminBtn">Home</button>
      <button id="patientAppointments" class="adminBtn">Appointments</button>
      <a href="#" id="logoutPatientLink">Logout</a>`;
  }

  headerDiv.innerHTML = `<header class="header">${logo}<nav>${nav}</nav></header>`;
  attachHeaderButtonListeners();
}

// "Add Doctor", "Login" and "Sign Up" open modals, so their listeners are attached by each page's
// module script, which has access to openModal.
function attachHeaderButtonListeners() {
  const handlers = {
    logoutLink: logout,
    logoutPatientLink: logoutPatient,
    doctorHomeBtn: () => selectRole("doctor"),
    home: () => { window.location.href = "/pages/loggedPatientDashboard.html"; },
    patientAppointments: () => { window.location.href = "/pages/patientAppointments.html"; }
  };
  for (const [id, handler] of Object.entries(handlers)) {
    const element = document.getElementById(id);
    if (element) {
      element.addEventListener("click", (event) => {
        event.preventDefault();
        handler();
      });
    }
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

renderHeader();
