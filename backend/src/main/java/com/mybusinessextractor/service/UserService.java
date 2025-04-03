package com.mybusinessextractor.service;

import com.mybusinessextractor.dto.AuthResponse;
import com.mybusinessextractor.dto.LoginRequest;
import com.mybusinessextractor.dto.UserRegistrationRequest;
import com.mybusinessextractor.entity.UserEntity;

/**
 * Service interface for user operations.
 */
public interface UserService {
    
    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return auth response with token and user info
     */
    AuthResponse registerUser(UserRegistrationRequest request);
    
    /**
     * Login a user.
     *
     * @param request the login request
     * @return auth response with token and user info
     */
    AuthResponse loginUser(LoginRequest request);
    
    /**
     * Verify a user's email address.
     *
     * @param token the verification token
     * @return true if verified successfully, false otherwise
     */
    boolean verifyEmail(String token);
    
    /**
     * Request a password reset.
     *
     * @param email the user's email
     * @return true if request was successful, false otherwise
     */
    boolean requestPasswordReset(String email);
    
    /**
     * Reset a user's password.
     *
     * @param token the reset token
     * @param newPassword the new password
     * @return true if reset successfully, false otherwise
     */
    boolean resetPassword(String token, String newPassword);
    
    /**
     * Get a user by username.
     *
     * @param username the username
     * @return the user entity
     */
    UserEntity getUserByUsername(String username);
    
    /**
     * Get a user by email.
     *
     * @param email the email
     * @return the user entity
     */
    UserEntity getUserByEmail(String email);
} 