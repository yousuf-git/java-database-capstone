package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;

    public AppointmentController(AppointmentService appointmentService, Service service, TokenService tokenService,
                                 PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.copyOf(validation.getBody()), validation.getStatusCode());
        }
        return new ResponseEntity<>(appointmentService.getAppointments(date, patientName, token), HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody @Valid Appointment appointment,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        Map<String, String> response = new HashMap<>();
        // The appointment is always booked for the patient the token belongs to, never for the id in the body.
        Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
        appointment.setPatient(patient);

        int valid = service.validateAppointment(appointment);
        if (valid == -1) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (valid == 0) {
            response.put("message", "That time slot is not available");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (appointmentService.bookAppointment(appointment) == 1) {
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        response.put("message", "Some internal error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody @Valid Appointment appointment,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }
        return appointmentService.updateAppointment(appointment, token);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }
        return appointmentService.cancelAppointment(id, token);
    }
}
