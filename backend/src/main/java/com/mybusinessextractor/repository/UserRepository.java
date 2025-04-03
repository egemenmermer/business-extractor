package com.mybusinessextractor.repository;

import com.mybusinessextractor.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return optional containing the user if found
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Find a user by username or email.
     *
     * @param username the username to search for
     * @param email the email to search for
     * @return optional containing the user if found
     */
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    
    /**
     * Find a user by verification token.
     *
     * @param token the verification token to search for
     * @return optional containing the user if found
     */
    Optional<UserEntity> findByVerificationToken(String token);
    
    /**
     * Find a user by reset token.
     *
     * @param token the reset token to search for
     * @return optional containing the user if found
     */
    Optional<UserEntity> findByResetToken(String token);
    
    /**
     * Check if a username exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);
} 