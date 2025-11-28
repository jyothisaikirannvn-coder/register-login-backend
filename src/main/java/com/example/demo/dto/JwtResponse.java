package com.example.demo.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String role;
    private String userId;  // ✅ ADDED userId FIELD

    // ✅ UPDATED CONSTRUCTOR - ADD userId PARAMETER
    public JwtResponse(String token, String email, String role, String userId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userId = userId;  // ✅ SAVE userId
    }

    // ✅ EXISTING GETTERS/SETTERS (UNCHANGED)
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ✅ NEW userId GETTER/SETTER
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}