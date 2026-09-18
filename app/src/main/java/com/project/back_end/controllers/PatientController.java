package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.copyOf(validation.getBody()), validation.getStatusCode());
        }
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody @Valid Patient patient) {
        Map<String, String> response = new HashMap<>();
        if (!service.validatePatient(patient)) {
            response.put("message", "Patient with email id or phone no already exist");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (patientService.createPatient(patient) == 1) {
            response.put("message", "Signup successful");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        response.put("message", "Some internal error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id, @PathVariable String user,
                                                                     @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.copyOf(validation.getBody()), validation.getStatusCode());
        }
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.copyOf(validation.getBody()), validation.getStatusCode());
        }
        return service.filterPatient(condition, name, token);
    }
}
