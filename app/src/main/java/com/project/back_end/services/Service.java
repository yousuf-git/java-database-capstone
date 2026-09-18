package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                   PatientRepository patientRepository, DoctorService doctorService, PatientService patientService,
                   PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String role) {
        Map<String, String> response = new HashMap<>();
        if (tokenService.validateToken(token, role)) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.put("message", "Session expired or invalid login. Please log in again.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null || !passwordEncoder.matches(receivedAdmin.getPassword(), admin.getPassword())) {
                response.put("message", "Invalid username or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            response.put("token", tokenService.generateToken(admin.getUsername()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Something went wrong while logging in");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // The frontend sends the literal "null" for filters the user left empty.
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        String doctorName = valueOrNull(name);
        String doctorSpecialty = valueOrNull(specialty);
        String period = valueOrNull(time);

        List<Doctor> doctors;
        if (doctorName != null && doctorSpecialty != null && period != null) {
            doctors = doctorService.filterDoctorsByNameSpecilityandTime(doctorName, doctorSpecialty, period);
        } else if (doctorName != null && doctorSpecialty != null) {
            doctors = doctorService.filterDoctorByNameAndSpecility(doctorName, doctorSpecialty);
        } else if (doctorName != null && period != null) {
            doctors = doctorService.filterDoctorByNameAndTime(doctorName, period);
        } else if (doctorSpecialty != null && period != null) {
            doctors = doctorService.filterDoctorByTimeAndSpecility(doctorSpecialty, period);
        } else if (doctorName != null) {
            doctors = doctorService.findDoctorByName(doctorName);
        } else if (doctorSpecialty != null) {
            doctors = doctorService.filterDoctorBySpecility(doctorSpecialty);
        } else if (period != null) {
            doctors = doctorService.filterDoctorsByTime(period);
        } else {
            doctors = doctorService.getDoctors();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return -1;
        }
        List<String> available = doctorService.getDoctorAvailability(doctor.get().getId(),
                appointment.getAppointmentTime().toLocalDate());
        boolean free = available.stream()
                .anyMatch(slot -> slot.startsWith(appointment.getAppointmentTime().toLocalTime().toString()));
        return free ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null || !passwordEncoder.matches(login.getPassword(), patient.getPassword())) {
                response.put("message", "Invalid email or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            response.put("token", tokenService.generateToken(patient.getEmail()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Something went wrong while logging in");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        String patientCondition = valueOrNull(condition);
        String doctorName = valueOrNull(name);
        if (patientCondition != null && doctorName != null) {
            return patientService.filterByDoctorAndCondition(patientCondition, doctorName, patient.getId());
        }
        if (patientCondition != null) {
            return patientService.filterByCondition(patientCondition, patient.getId());
        }
        if (doctorName != null) {
            return patientService.filterByDoctor(doctorName, patient.getId());
        }
        return patientService.getPatientAppointment(patient.getId(), token);
    }

    private String valueOrNull(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }
}
