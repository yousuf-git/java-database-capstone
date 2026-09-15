// Sample prescriptions for the Smart Clinic MongoDB database.
//
// Run with:
//   mongosh "mongodb://localhost:27017" database/prescriptions.js
//
// Each prescription belongs to a completed appointment from database/sample-data.sql, and
// patientName matches that appointment's patient. createdAt is the appointment's date, relative
// to the day the script is run, to line up with the MySQL sample data.

db = db.getSiblingDB("prescriptions");

if (db.prescriptions.countDocuments() > 0) {
  print("The prescriptions collection already has data; nothing inserted.");
  quit();
}

function daysAgo(days) {
  const date = new Date();
  date.setUTCHours(0, 0, 0, 0);
  date.setUTCDate(date.getUTCDate() - days);
  return date;
}

// Same name as the index Spring Data creates from @Indexed, so the two never conflict.
db.prescriptions.createIndex({ appointmentId: 1 }, { name: "appointmentId" });

const result = db.prescriptions.insertMany([
  {
    patientName: "John Smith",
    appointmentId: NumberLong("1"),
    medication: "Atorvastatin",
    dosage: "10 mg once daily at night",
    doctorNotes: "Recheck cholesterol in 3 months.",
    refillCount: 2,
    pharmacy: { name: "City Care Pharmacy", location: "12 Market Street" },
    tags: ["cholesterol", "cardiology"],
    createdAt: daysAgo(58)
  },
  {
    patientName: "Maria Gonzalez",
    appointmentId: NumberLong("2"),
    medication: "Sumatriptan",
    dosage: "50 mg at migraine onset, max 2 per day",
    doctorNotes: "Keep a headache diary.",
    refillCount: 1,
    tags: ["migraine"],
    createdAt: daysAgo(55)
  },
  {
    patientName: "Ahmed Khan",
    appointmentId: NumberLong("3"),
    medication: "Ibuprofen",
    dosage: "400 mg every 8 hours after meals for 5 days",
    tags: ["pain"],
    createdAt: daysAgo(52)
  },
  {
    patientName: "Priya Sharma",
    appointmentId: NumberLong("4"),
    medication: "Paracetamol",
    dosage: "250 mg every 6 hours as needed for fever",
    doctorNotes: "Plenty of fluids and rest.",
    pharmacy: { name: "HealthPlus Pharmacy", location: "5 Station Road" },
    tags: ["fever", "pediatrics"],
    createdAt: daysAgo(50)
  },
  {
    patientName: "Sofia Rossi",
    appointmentId: NumberLong("6"),
    medication: "Hydrocortisone cream 1%",
    dosage: "Apply thin layer twice daily for 7 days",
    refillCount: 1,
    tags: ["dermatology"],
    createdAt: daysAgo(44)
  },
  {
    patientName: "Chen Wei",
    appointmentId: NumberLong("7"),
    medication: "Amoxicillin",
    dosage: "500 mg three times a day for 7 days",
    doctorNotes: "Complete the full course.",
    pharmacy: { name: "City Care Pharmacy", location: "12 Market Street" },
    tags: ["antibiotic", "infection"],
    createdAt: daysAgo(41)
  },
  {
    patientName: "Fatima Noor",
    appointmentId: NumberLong("8"),
    medication: "Amlodipine",
    dosage: "5 mg once daily in the morning",
    doctorNotes: "Monitor blood pressure at home.",
    refillCount: 3,
    tags: ["hypertension", "cardiology"],
    createdAt: daysAgo(38)
  },
  {
    patientName: "Emma Davis",
    appointmentId: NumberLong("10"),
    medication: "Sertraline",
    dosage: "50 mg once daily",
    doctorNotes: "Follow-up in 4 weeks to review response.",
    refillCount: 1,
    tags: ["mental health"],
    createdAt: daysAgo(33)
  },
  {
    patientName: "John Smith",
    appointmentId: NumberLong("12"),
    medication: "Cetirizine",
    dosage: "10 mg once daily for 14 days",
    tags: ["allergy"],
    createdAt: daysAgo(27)
  },
  {
    patientName: "Emma Davis",
    appointmentId: NumberLong("20"),
    medication: "Omeprazole",
    dosage: "20 mg once daily before breakfast for 4 weeks",
    doctorNotes: "Avoid spicy food.",
    pharmacy: { name: "Wellness Pharmacy", location: "40 Hill Road" },
    tags: ["gastric"],
    createdAt: daysAgo(3)
  }
]);

print(`Inserted ${Object.keys(result.insertedIds).length} prescriptions.`);
