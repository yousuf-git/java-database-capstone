package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository,
                          TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public int createPatient(Patient patient) {
        try {
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!ownsRecord(id, token)) {
                response.put("message", "You can only view your own appointments");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            response.put("appointments", toDtos(appointmentRepository.findByPatientId(id)));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Something went wrong while loading appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        Integer status = statusOf(condition);
        if (status == null) {
            response.put("message", "Condition must be either 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.put("appointments",
                toDtos(appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        response.put("appointments", toDtos(appointmentRepository.filterByDoctorNameAndPatientId(name, patientId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name,
                                                                         Long patientId) {
        Map<String, Object> response = new HashMap<>();
        Integer status = statusOf(condition);
        if (status == null) {
            response.put("message", "Condition must be either 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.put("appointments",
                toDtos(appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
            if (patient == null) {
                response.put("message", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("patient", patient);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Something went wrong while loading your details");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // A patient may only read their own record; a doctor's token passes because doctors need the
    // records of patients they treat.
    private boolean ownsRecord(Long patientId, String token) {
        Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
        return patient == null || patient.getId().equals(patientId);
    }

    private Integer statusOf(String condition) {
        if ("past".equalsIgnoreCase(condition)) {
            return 1;
        }
        return "future".equalsIgnoreCase(condition) ? 0 : null;
    }

    private List<AppointmentDTO> toDtos(List<Appointment> appointments) {
        return appointments.stream().map(AppointmentService::toDto).toList();
    }
}
