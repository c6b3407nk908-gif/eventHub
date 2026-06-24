package com.eventmate.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String name, String token);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendPasswordResetOtpEmail(String to, String otp);
}
