package com.auth.jsid.gmailService;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.auth.jsid.userRepository.UserRepository;

@Service
public class GmailService {

    private final JavaMailSender gmailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    UserRepository userRepository;

    public GmailService(JavaMailSender gmailSender) {
        this.gmailSender = gmailSender;
    }

    public void sendOtpAdmin(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("javadeveloper@gmail.com");
        message.setTo(email);
        message.setText("Your Verification code is:" + otp);
        message.setSubject("OTP Verification Code");
        gmailSender.send(message);
    }
    
}
