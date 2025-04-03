package com.mybusinessextractor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for password reset email requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestEmail {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
} 