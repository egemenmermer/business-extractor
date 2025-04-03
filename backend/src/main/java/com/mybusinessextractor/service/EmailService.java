package com.mybusinessextractor.service;

/**
 * Service interface for email operations.
 */
public interface EmailService {
    
    /**
     * Send a simple email.
     *
     * @param to the recipient
     * @param subject the email subject
     * @param text the email body
     * @return true if sent successfully, false otherwise
     */
    boolean sendSimpleEmail(String to, String subject, String text);
    
    /**
     * Send an HTML email.
     *
     * @param to the recipient
     * @param subject the email subject
     * @param htmlBody the HTML email body
     * @return true if sent successfully, false otherwise
     */
    boolean sendHtmlEmail(String to, String subject, String htmlBody);
    
    /**
     * Send a verification email.
     *
     * @param to the recipient
     * @param token the verification token
     * @param name the recipient's name
     * @return true if sent successfully, false otherwise
     */
    boolean sendVerificationEmail(String to, String token, String name);
    
    /**
     * Send a password reset email.
     *
     * @param to the recipient
     * @param token the reset token
     * @param name the recipient's name
     * @return true if sent successfully, false otherwise
     */
    boolean sendPasswordResetEmail(String to, String token, String name);
} 