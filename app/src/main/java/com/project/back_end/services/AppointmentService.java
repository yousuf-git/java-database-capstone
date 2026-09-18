package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final DoctorService doctorService;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                              DoctorRepository doctorRepository, TokenService tokenService,
                              DoctorService doctorService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.doctorService = doctorService;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
        if (patient == null || !patient.getId().equals(existing.get().getPatient().getId())) {
            response.put("message", "This appointment belongs to another patient");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (!doctorService.isSlotStart(doctor.get(), appointment.getAppointmentTime())) {
            response.put("message", "The doctor is not available at the requested time");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (isSlotTaken(doctor.get().getId(), appointment.getAppointmentTime().toLocalDate(),
                appointment.getAppointmentTime().toLocalTime(), appointment.getId())) {
            response.put("message", "That time slot is already booked");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor.get());
        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Patient patient = patientRepository.findByEmail(tokenService.extractIdentifier(token));
        if (patient == null || !patient.getId().equals(appointment.get().getPatient().getId())) {
            response.put("message", "This appointment belongs to another patient");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        appointmentRepository.delete(appointment.get());
        response.put("message", "Appointment cancelled successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointments(LocalDate date, String patientName, String token) {
        Doctor doctor = doctorRepository.findByEmail(tokenService.extractIdentifier(token));
        Map<String, Object> response = new HashMap<>();
        if (doctor == null) {
            response.put("appointments", List.of());
            return response;
        }

        List<Appointment> appointments = (patientName == null || patientName.isBlank()
                || "null".equalsIgnoreCase(patientName))
                ? appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctor.getId(), date.atStartOfDay(), date.atTime(LocalTime.MAX))
                : appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctor.getId(), patientName, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        response.put("appointments", appointments.stream().map(AppointmentService::toDto).toList());
        return response;
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }

    private boolean isSlotTaken(Long doctorId, LocalDate date, LocalTime time, Long excludedAppointmentId) {
        return appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, date.atStartOfDay(), date.atTime(LocalTime.MAX))
                .stream()
                .anyMatch(booked -> booked.getAppointmentTime().toLocalTime().equals(time)
                        && !booked.getId().equals(excludedAppointmentId));
    }

    public static AppointmentDTO toDto(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        return new AppointmentDTO(appointment.getId(), doctor.getId(), doctor.getName(), patient.getId(),
                patient.getName(), patient.getEmail(), patient.getPhone(), patient.getAddress(),
                appointment.getAppointmentTime(), appointment.getStatus());
    }
}
