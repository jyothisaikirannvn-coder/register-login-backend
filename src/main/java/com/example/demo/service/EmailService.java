package com.example.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${smtp.from:onboarding@resend.dev}")
    private String from;

    public void sendOtp(String to, String otp) {
        if (apiKey.isBlank()) {
            throw new RuntimeException("RESEND_API_KEY not configured");
        }

        // Build Resend email request body
        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("from", to + " via <onboarding@resend.dev>");
        emailRequest.put("to", Collections.singletonList(to));  // List for single recipient
        emailRequest.put("subject", "Your OTP Code");
        emailRequest.put("text", "Your OTP is: " + otp + "\nThis code expires in 10 minutes.");

        try {
            // Headers: JSON + Bearer auth
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Convert to JSON string
            String jsonBody;
            try {
                jsonBody = objectMapper.writeValueAsString(emailRequest);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize email request: " + e.getMessage());
            }

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // Send POST to Resend API
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.resend.com/emails", entity, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ OTP email sent successfully via Resend API");
            } else {
                throw new RuntimeException("Resend API failed with status " + response.getStatusCode() + ": " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email via Resend: " + e.getMessage(), e);
        }
    }
}