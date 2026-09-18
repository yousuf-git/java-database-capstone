package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository,
                         TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty()) {
            return List.of();
        }
        Set<LocalTime> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, date.atStartOfDay(), date.atTime(LocalTime.MAX))
                .stream()
                .map(appointment -> appointment.getAppointmentTime().toLocalTime())
                .collect(Collectors.toSet());

        return doctor.get().getAvailableTimes().stream()
                .filter(slot -> !booked.contains(startTimeOf(slot)))
                .toList();
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) {
                return -1;
            }
            // An update without a new password keeps the stored hash.
            if (doctor.getPassword() == null || doctor.getPassword().isBlank()) {
                doctor.setPassword(existing.get().getPassword());
            } else {
                doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doctor -> doctor.getAvailableTimes().size());
        return doctors;
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (doctorRepository.findById(id).isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if (doctor == null || !passwordEncoder.matches(login.getPassword(), doctor.getPassword())) {
                response.put("message", "Invalid email or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            response.put("token", tokenService.generateToken(doctor.getEmail()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Something went wrong while logging in");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors.forEach(doctor -> doctor.getAvailableTimes().size());
        return doctors;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String period) {
        return filterDoctorByTime(
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty), period);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String period) {
        List<Doctor> filtered = new ArrayList<>();
        for (Doctor doctor : doctors) {
            boolean matches = doctor.getAvailableTimes().stream().anyMatch(slot -> matchesPeriod(slot, period));
            if (matches) {
                filtered.add(doctor);
            }
        }
        return filtered;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String period) {
        return filterDoctorByTime(findDoctorByName(name), period);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors.forEach(doctor -> doctor.getAvailableTimes().size());
        return doctors;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String period) {
        return filterDoctorByTime(filterDoctorBySpecility(specialty), period);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors.forEach(doctor -> doctor.getAvailableTimes().size());
        return doctors;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String period) {
        return filterDoctorByTime(getDoctors(), period);
    }

    private boolean matchesPeriod(String slot, String period) {
        boolean morning = startTimeOf(slot).getHour() < 12;
        return "AM".equalsIgnoreCase(period) == morning;
    }

    private LocalTime startTimeOf(String slot) {
        return LocalTime.parse(slot.split("-")[0].trim());
    }

    // Kept for callers that only have the slot string, such as appointment validation.
    public boolean isSlotStart(Doctor doctor, LocalDateTime appointmentTime) {
        LocalTime requested = appointmentTime.toLocalTime();
        return doctor.getAvailableTimes().stream().anyMatch(slot -> startTimeOf(slot).equals(requested));
    }
}
