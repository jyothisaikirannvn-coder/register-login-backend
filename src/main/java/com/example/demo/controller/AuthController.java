package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    // Configure cookie security behavior with an env variable (default true in prod)
    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.domain:#{null}}")
    private String cookieDomain;

    public AuthController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            String jwt = userService.login(loginRequest);
            User user = userService.getUserByEmail(loginRequest.getEmail());

            Cookie jwtCookie = new Cookie("jwtToken", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(cookieSecure);  // true in production; false allowed in dev
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400);  // 24 hours

            if (cookieDomain != null && !cookieDomain.isBlank()) {
                jwtCookie.setDomain(cookieDomain);
            }

            // IMPORTANT: SameSite attribute via response header (Servlet API lacks direct setter in older versions)
            response.addCookie(jwtCookie);
            // set SameSite explicitly (Render/modern browsers prefer Lax or None if cross-site)
            // If cookieSecure == true and you need cross-site, use SameSite=None; Secure must be true then
            String sameSite = cookieSecure ? "None" : "Lax";
            response.setHeader("Set-Cookie",
                    String.format("jwtToken=%s; HttpOnly; Path=/; Max-Age=86400; Secure=%s; SameSite=%s%s",
                            jwt,
                            cookieSecure ? "true" : "false",
                            sameSite,
                            (cookieDomain != null && !cookieDomain.isBlank()) ? "; Domain=" + cookieDomain : ""
                    )
            );

            return ResponseEntity.ok(new JwtResponse(jwt, user.getEmail(), user.getRole()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }
    }
}
