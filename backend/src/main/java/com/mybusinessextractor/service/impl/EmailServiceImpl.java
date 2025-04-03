package com.mybusinessextractor.service.impl;

import com.mybusinessextractor.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementation of the EmailService for sending emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.url}")
    private String appUrl;
    
    @Override
    public boolean sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            return false;
        }
    }
    
    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            return false;
        }
    }
    
    @Override
    public boolean sendVerificationEmail(String to, String token, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationUrl", appUrl + "/verify-email?token=" + token);
            
            String htmlBody = templateEngine.process("email/verification", context);
            
            return sendHtmlEmail(to, "Verify your email address", htmlBody);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            return false;
        }
    }
    
    @Override
    public boolean sendPasswordResetEmail(String to, String token, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetUrl", appUrl + "/reset-password?token=" + token);
            
            String htmlBody = templateEngine.process("email/password-reset", context);
            
            return sendHtmlEmail(to, "Reset your password", htmlBody);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            return false;
        }
    }
} 