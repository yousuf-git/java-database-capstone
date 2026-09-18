package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class TokenService {

    private static final Duration TOKEN_VALIDITY = Duration.ofDays(7);

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final String secret;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository,
                        PatientRepository patientRepository, @Value("${jwt.secret}") String secret) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.secret = secret;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // The subject is the admin's username, or the doctor's or patient's email.
    public String generateToken(String identifier) {
        Date issuedAt = new Date();
        return Jwts.builder()
                .subject(identifier)
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + TOKEN_VALIDITY.toMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractIdentifier(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String role) {
        try {
            String identifier = extractIdentifier(token);
            return switch (role) {
                case "admin" -> adminRepository.findByUsername(identifier) != null;
                case "doctor" -> doctorRepository.findByEmail(identifier) != null;
                case "patient" -> patientRepository.findByEmail(identifier) != null;
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }
}
