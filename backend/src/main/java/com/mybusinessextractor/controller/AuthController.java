package com.mybusinessextractor.controller;

import com.mybusinessextractor.dto.*;
import com.mybusinessextractor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return auth response with token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        AuthResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Login a user.
     *
     * @param request the login request
     * @return auth response with token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verify a user's email address.
     *
     * @param token the verification token
     * @return success message
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        log.info("Verifying email with token: {}", token);
        boolean verified = userService.verifyEmail(token);
        
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verified successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Request a password reset.
     *
     * @param request the password reset request email
     * @return success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody PasswordResetRequestEmail request) {
        log.info("Password reset requested for email: {}", request.getEmail());
        boolean sent = userService.requestPasswordReset(request.getEmail());
        
        if (!sent) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send password reset email");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset a user's password.
     *
     * @param request the password reset request
     * @return success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        log.info("Resetting password with token: {}", request.getToken());
        boolean reset = userService.resetPassword(request.getToken(), request.getNewPassword());
        
        if (!reset) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reset token");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok(response);
    }
} 