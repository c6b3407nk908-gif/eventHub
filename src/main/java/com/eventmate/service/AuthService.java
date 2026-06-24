package com.eventmate.service;

import com.eventmate.dto.request.ResendVerificationRequestDTO;
import com.eventmate.dto.request.VerifyOtpRequestDTO;
import com.eventmate.dto.request.ForgotPasswordRequestDTO;
import com.eventmate.dto.request.ResetPasswordRequestDTO;
import com.eventmate.dto.response.VerificationResponseDTO;
import com.eventmate.model.User;
import java.util.Map;

public interface AuthService {
    Map<String, Object> registerUser(User signUpRequest);
    Map<String, Object> authenticateUser(User loginRequest);
    VerificationResponseDTO verifyEmail(VerifyOtpRequestDTO request);
    VerificationResponseDTO resendVerification(ResendVerificationRequestDTO request);
    Map<String, Object> forgotPassword(ForgotPasswordRequestDTO request);
    Map<String, Object> verifyPasswordResetOtp(com.eventmate.dto.request.VerifyOtpRequestDTO request);
    Map<String, Object> resetPassword(ResetPasswordRequestDTO request);
}
