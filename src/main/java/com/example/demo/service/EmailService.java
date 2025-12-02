package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${smtp.from}")
    private String fromEmail;

    public void sendOtp(String to, String otp) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("BREVO_API_KEY is missing!");
        }

        Map<String, Object> request = Map.of(
            "sender", Map.of("name", "Your App OTP", "email", fromEmail),
            "to", Map.of("email", to),
            "subject", "Your OTP Code",
            "htmlContent", "<h2>Your OTP is <strong style='font-size: 24px; color: #007bff;'>" + otp + "</strong></h2><p>It expires in 10 minutes.</p>",
            "textContent", "Your OTP is: " + otp + "\nExpires in 10 minutes."
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            String jsonBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.brevo.com/v3/smtp/email", entity, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("OTP email sent successfully to " + to + " via Brevo");
            } else {
                throw new RuntimeException("Brevo error: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}