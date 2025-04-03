package com.mybusinessextractor.service.impl;

import com.mybusinessextractor.dto.AuthResponse;
import com.mybusinessextractor.dto.LoginRequest;
import com.mybusinessextractor.dto.UserRegistrationRequest;
import com.mybusinessextractor.entity.UserEntity;
import com.mybusinessextractor.repository.UserRepository;
import com.mybusinessextractor.service.EmailService;
import com.mybusinessextractor.service.UserService;
import com.mybusinessextractor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Implementation of the UserService for user operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return auth response with token and user info
     */
    @Transactional
    @Override
    public AuthResponse registerUser(UserRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        
        // Create verification token
        String verificationToken = UUID.randomUUID().toString();
        
        // Create user entity
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(false)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();
        
        // Save user
        user = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationToken,
                user.getFirstName()
        );
        
        // Create UserDetails for token generation
        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
        
        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);
        
        // Return response
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerified(user.isEmailVerified())
                .token(token)
                .message("Registration successful. Please check your email to verify your account.")
                .build();
    }
    
    /**
     * Login a user.
     *
     * @param request the login request
     * @return auth response with token and user info
     */
    @Override
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );
            
            // Get UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Find user by username or email
            UserEntity user = userRepository.findByUsernameOrEmail(
                    request.getUsernameOrEmail(), request.getUsernameOrEmail()
            ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            
            // Return response
            return AuthResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .emailVerified(user.isEmailVerified())
                    .token(token)
                    .message("Login successful")
                    .build();
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");
        }
    }
    
    /**
     * Verify a user's email address.
     *
     * @param token the verification token
     * @return true if verified successfully, false otherwise
     */
    @Transactional
    @Override
    public boolean verifyEmail(String token) {
        // Find user by verification token
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid verification token"));
        
        // Update user
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Request a password reset.
     *
     * @param email the user's email
     * @return true if request was successful, false otherwise
     */
    @Transactional
    @Override
    public boolean requestPasswordReset(String email) {
        // Find user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        
        // Update user
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        
        // Send reset email
        return emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetToken,
                user.getFirstName()
        );
    }
    
    /**
     * Reset a user's password.
     *
     * @param token the reset token
     * @param newPassword the new password
     * @return true if reset successfully, false otherwise
     */
    @Transactional
    @Override
    public boolean resetPassword(String token, String newPassword) {
        // Find user by reset token
        UserEntity user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid reset token"));
        
        // Check if token is expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired");
        }
        
        // Update user
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Get a user by username.
     *
     * @param username the username
     * @return the user entity
     */
    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
    }
    
    /**
     * Get a user by email.
     *
     * @param email the email
     * @return the user entity
     */
    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
    }
} 