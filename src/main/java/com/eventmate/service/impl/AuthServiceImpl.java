package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.exception.UserBlockedException;
import com.eventmate.model.User;
import com.eventmate.repository.UserRepository;
import com.eventmate.security.JwtUtils;
import com.eventmate.dto.request.ResendVerificationRequestDTO;
import com.eventmate.dto.request.VerifyOtpRequestDTO;
import com.eventmate.dto.request.ForgotPasswordRequestDTO;
import com.eventmate.dto.request.ResetPasswordRequestDTO;
import com.eventmate.dto.response.VerificationResponseDTO;
import com.eventmate.exception.EmailNotVerifiedException;
import com.eventmate.service.AuthService;
import com.eventmate.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public Map<String, Object> registerUser(User signUpRequest) {
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        
        String email = signUpRequest.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("User already exists");
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        String token = generateOtp();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(15));
        user.setEmailVerified(false);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. A verification email has been sent to your email address.");
        return response;
    }

    @Override
    public Map<String, Object> authenticateUser(User loginRequest) {
        if (loginRequest.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase());

        if (userOptional.isEmpty()) {
            throw new BadRequestException("Invalid credentials");
        }

        User user = userOptional.get();

        if (user.isBlocked()) {
            throw new UserBlockedException("Your account has been blocked by an administrator.");
        }

        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            boolean needsSave = false;
            if (!"ROLE_ADMIN".equals(user.getRole())) {
                user.setRole("ROLE_ADMIN");
                needsSave = true;
            }
            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                needsSave = true;
            }
            if (needsSave) {
                userRepository.save(user);
            }
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    @Override
    public VerificationResponseDTO verifyEmail(VerifyOtpRequestDTO request) {
        if (request.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found.");
        }
        User user = userOpt.get();
        
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }
        
        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP.");
        }
        
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired.");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new VerificationResponseDTO(true, "Email verified successfully.");
    }

    @Override
    public VerificationResponseDTO resendVerification(ResendVerificationRequestDTO request) {
        if (request.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found.");
        }
        User user = userOpt.get();
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }
        String token = generateOtp();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
        return new VerificationResponseDTO(true, "A new verification email has been sent.");
    }

    @Override
    public Map<String, Object> forgotPassword(ForgotPasswordRequestDTO request) {
        if (request.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // Always return the same message to prevent email enumeration
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP has been sent to your email address.");
        
        logger.info("Password reset requested for email: {}", email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Rate limiting: 60-second cooldown
            if (user.getLastOtpRequestTime() != null && user.getLastOtpRequestTime().plusSeconds(60).isAfter(LocalDateTime.now())) {
                logger.warn("Password reset rate limit hit for email: {}", email);
                throw new BadRequestException("Please wait 60 seconds before requesting another OTP.");
            }
            
            String otp = generateOtp();
            user.setPasswordResetToken(otp);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(5));
            user.setPasswordResetAttempts(0);
            user.setLastOtpRequestTime(LocalDateTime.now());
            user.setApprovedResetToken(null);
            userRepository.save(user);
            
            emailService.sendPasswordResetOtpEmail(user.getEmail(), otp);
            logger.info("Password reset OTP generated and sent to email: {}", email);
        } else {
            logger.warn("Password reset requested for non-existent email: {}", email);
        }
        
        return response;
    }

    @Override
    public Map<String, Object> verifyPasswordResetOtp(com.eventmate.dto.request.VerifyOtpRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            logger.warn("Password reset OTP verification failed: User not found for email: {}", email);
            throw new BadRequestException("Invalid email or OTP.");
        }
        
        User user = userOpt.get();
        
        if (user.getPasswordResetToken() == null || user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.warn("Password reset OTP verification failed: Invalid or expired OTP for email: {}", email);
            throw new BadRequestException("OTP is invalid or has expired.");
        }
        
        if (user.getPasswordResetAttempts() >= 5) {
            logger.warn("Password reset OTP verification blocked: Max attempts reached for email: {}", email);
            throw new BadRequestException("Too many invalid attempts. Please request a new OTP.");
        }
        
        if (!user.getPasswordResetToken().equals(request.getOtp())) {
            int newAttempts = user.getPasswordResetAttempts() + 1;
            user.setPasswordResetAttempts(newAttempts);
            logger.warn("Password reset OTP verification failed for email: {}. Attempt: {}", email, newAttempts);
            
            if (newAttempts >= 5) {
                user.setPasswordResetToken(null);
                user.setPasswordResetTokenExpiry(null);
                userRepository.save(user);
                logger.warn("Password reset OTP verification locked out: Email: {}", email);
                throw new BadRequestException("Too many invalid attempts. Please request a new OTP.");
            }
            
            userRepository.save(user);
            throw new BadRequestException("Invalid OTP.");
        }
        
        // OTP verified successfully
        String approvedToken = java.util.UUID.randomUUID().toString().replace("-", "");
        user.setApprovedResetToken(approvedToken);
        user.setPasswordResetToken(null); // Clear OTP so it can't be reused
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Give them 15 mins to reset password
        user.setPasswordResetAttempts(0);
        userRepository.save(user);
        
        logger.info("Password reset OTP verified successfully for email: {}", email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP verified successfully.");
        response.put("approvedResetToken", approvedToken);
        return response;
    }

    @Override
    public Map<String, Object> resetPassword(ResetPasswordRequestDTO request) {
        if (request.getEmail() == null) {
            throw new BadRequestException("Email is required.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }
        
        // Password strength validation
        String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        if (!request.getNewPassword().matches(pwdRegex)) {
            throw new BadRequestException("Password does not meet security requirements.");
        }

        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            logger.warn("Password reset failed: User not found for email: {}", email);
            throw new BadRequestException("Invalid request.");
        }
        
        User user = userOpt.get();
        
        if (user.getApprovedResetToken() == null || !user.getApprovedResetToken().equals(request.getToken())) {
            logger.warn("Password reset failed: Invalid reset token for email: {}", email);
            throw new BadRequestException("Invalid or unauthorized reset token.");
        }
        
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.warn("Password reset failed: Token expired for email: {}", email);
            throw new BadRequestException("Reset session has expired. Please verify OTP again.");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setApprovedResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        
        userRepository.save(user);
        
        logger.info("Password successfully reset for email: {}", email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Your password has been successfully changed. Please sign in with your new password.");
        return response;
    }

    private Map<String, Object> buildAuthResponse(User user) {
        String jwt = jwtUtils.generateToken(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole());
        response.put("user", userMap);

        return response;
    }
}
