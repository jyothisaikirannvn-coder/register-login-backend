package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtils jwtUtils;

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }
 // Add to UserService class
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public RegisterResponse register(RegisterRequest request) {

        // Duplicate checks
        if (userRepository.existsByEmail(request.getPersonalInfo().getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getAccount().getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String now = ISO_FORMATTER.format(Instant.now());

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());

        // --- PERSONAL INFO ---
        user.setEmail(request.getPersonalInfo().getEmail());
        user.setFirstName(request.getPersonalInfo().getFirstName());
        user.setLastName(request.getPersonalInfo().getLastName());
        user.setPhone(request.getPersonalInfo().getPhone());
        user.setDateOfBirth(request.getPersonalInfo().getDateOfBirth());

        // --- ACCOUNT INFO ---
        user.setUsername(request.getAccount().getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getAccount().getPassword()));

        // --- INVESTMENT INFO ---
        user.setRiskAppetite(request.getInvestmentProfile().getRiskAppetite());
        user.setExperience(request.getInvestmentProfile().getExperience());
        user.setInvestmentGoal(request.getInvestmentProfile().getInvestmentGoal());

        // --- META INFO ---
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        return new RegisterResponse(
                user.getUserId(),
                "User registered successfully"
        );
    }

    // New login method
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtils.generateJwtToken(user.getEmail());
    }
}