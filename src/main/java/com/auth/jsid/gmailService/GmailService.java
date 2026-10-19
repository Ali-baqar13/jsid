package com.auth.jsid.gmailService;

import java.time.Instant;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.auth.jsid.userRepository.UserRepository;

import lombok.AllArgsConstructor;

@Service

public class GmailService {

    private final JavaMailSender gmailSender;

    @Autowired
    UserRepository userRepository;

    private String last_otp;

    private Instant otTimeStamsp;

    public GmailService(JavaMailSender gmailSender, String Admin_Gmail_Id, String last_otp, Instant otTimeStamsp) {
        this.gmailSender = gmailSender;
        this.last_otp = last_otp;
        this.otTimeStamsp = otTimeStamsp;

        
    }

    public void sendOtpAdmin(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        String otp = String.format("%5d", new Random().nextInt(100000));
        this.last_otp = otp;
        this.otTimeStamsp = Instant.now();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("javadeveloper@gmail.com");
        message.setTo(email);
        message.setText("Your Verification code is:" + otp);
        message.setSubject("OTP Verification Code");
        gmailSender.send(message);
    }
    
}
