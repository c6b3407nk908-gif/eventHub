package com.eventmate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    
    @Indexed(unique = true)
    private String email;
    private String password;
    private String profilePictureUrl;
    private boolean blocked = false;
    private String role = "ROLE_USER";
    private Date createdAt;
    private Date updatedAt;
    
    private boolean emailVerified = false;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiry;
    
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;
    private int passwordResetAttempts = 0;
    private LocalDateTime lastOtpRequestTime;
    private String approvedResetToken;

    public User() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public LocalDateTime getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) { this.verificationTokenExpiry = verificationTokenExpiry; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public LocalDateTime getPasswordResetTokenExpiry() { return passwordResetTokenExpiry; }
    public void setPasswordResetTokenExpiry(LocalDateTime passwordResetTokenExpiry) { this.passwordResetTokenExpiry = passwordResetTokenExpiry; }

    public int getPasswordResetAttempts() { return passwordResetAttempts; }
    public void setPasswordResetAttempts(int passwordResetAttempts) { this.passwordResetAttempts = passwordResetAttempts; }

    public LocalDateTime getLastOtpRequestTime() { return lastOtpRequestTime; }
    public void setLastOtpRequestTime(LocalDateTime lastOtpRequestTime) { this.lastOtpRequestTime = lastOtpRequestTime; }

    public String getApprovedResetToken() { return approvedResetToken; }
    public void setApprovedResetToken(String approvedResetToken) { this.approvedResetToken = approvedResetToken; }
}
