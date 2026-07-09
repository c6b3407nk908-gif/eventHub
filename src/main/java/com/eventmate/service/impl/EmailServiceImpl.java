package com.eventmate.service.impl;

import com.eventmate.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String name, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your EventHub Verification Code");

            String htmlContent = String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>" +
                "    <div style='background-color: #3b82f6; padding: 20px; text-align: center; color: white;'>" +
                "      <h2>EventHub</h2>" +
                "    </div>" +
                "    <div style='padding: 20px; text-align: center;'>" +
                "      <h3>Hello %s,</h3>" +
                "      <p>Thank you for registering at EventHub!</p>" +
                "      <p>Please use the following 6-digit code to verify your email address and activate your account:</p>" +
                "      <div style='margin: 30px auto; padding: 15px; background-color: #f3f4f6; border-radius: 8px; display: inline-block; letter-spacing: 5px; font-size: 32px; font-weight: bold; color: #1e40af;'>" +
                "        %s" +
                "      </div>" +
                "      <p style='color: #666; font-size: 14px;'>This code will expire in 15 minutes.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>",
                name, token
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Reset Your EventHub Password");

            String resetUrl = "http://localhost:5000/reset_password.html?token=" + token;

            String htmlContent = String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>" +
                "    <div style='background-color: #3b82f6; padding: 20px; text-align: center; color: white;'>" +
                "      <h2>EventHub</h2>" +
                "    </div>" +
                "    <div style='padding: 20px; text-align: center;'>" +
                "      <h3>Password Reset Request</h3>" +
                "      <p>A password reset was requested for your account.</p>" +
                "      <p>Click the button below to reset your password:</p>" +
                "      <a href='%s' style='display: inline-block; margin: 20px auto; padding: 12px 24px; background-color: #1e40af; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Reset Password</a>" +
                "      <p style='color: #666; font-size: 14px;'>Or copy and paste this link into your browser:</p>" +
                "      <p style='color: #666; font-size: 12px; word-break: break-all;'>%s</p>" +
                "      <p style='color: #666; font-size: 14px; margin-top: 30px;'>This link will expire in 15 minutes.</p>" +
                "      <p style='color: #999; font-size: 12px;'>If you did not request a password reset, please ignore this email.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>",
                resetUrl, resetUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Password Reset Code");

            String htmlContent = String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>" +
                "    <div style='background-color: #3b82f6; padding: 20px; text-align: center; color: white;'>" +
                "      <h2>EventHub</h2>" +
                "    </div>" +
                "    <div style='padding: 20px; text-align: center;'>" +
                "      <h3>Password Reset Request</h3>" +
                "      <p>A password reset was requested for your account.</p>" +
                "      <p>Please use the following 6-digit code to reset your password:</p>" +
                "      <div style='margin: 30px auto; padding: 15px; background-color: #f3f4f6; border-radius: 8px; display: inline-block; letter-spacing: 5px; font-size: 32px; font-weight: bold; color: #1e40af;'>" +
                "        %s" +
                "      </div>" +
                "      <p style='color: #666; font-size: 14px;'>This code will expire in 5 minutes.</p>" +
                "      <p style='color: #999; font-size: 12px;'>If you did not request a password reset, please ignore this email.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>",
                otp
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send password reset OTP email: " + e.getMessage());
        }
    }
}
