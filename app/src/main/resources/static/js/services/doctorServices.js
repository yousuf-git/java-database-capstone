// doctorServices.js
import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors ?? [];
  } catch (error) {
    console.error("Error :: getDoctors ::", error);
    return [];
  }
}

export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, { method: "DELETE" });
    const data = await response.json();
    return { success: response.ok, message: data.message };
  } catch (error) {
    console.error("Error :: deleteDoctor ::", error);
    return { success: false, message: "Network error. Please try again later." };
  }
}

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });
    const data = await response.json();
    return { success: response.ok, message: data.message };
  } catch (error) {
    console.error("Error :: saveDoctor ::", error);
    return { success: false, message: "Network error. Please try again later." };
  }
}

// The backend expects the literal string "null" for any filter that is not set.
export async function filterDoctors(name, time, specialty) {
  const segments = [name, time, specialty].map(value => encodeURIComponent(value ?? "null"));
  try {
    const response = await fetch(`${DOCTOR_API}/filter/${segments.join("/")}`);
    if (response.ok) {
      return await response.json();
    }
    console.error("Failed to filter doctors:", response.statusText);
    return { doctors: [] };
  } catch (error) {
    console.error("Error :: filterDoctors ::", error);
    alert("Something went wrong while filtering doctors.");
    return { doctors: [] };
  }
}
