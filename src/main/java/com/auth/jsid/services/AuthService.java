package com.auth.jsid.services;

import java.time.LocalDateTime;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

import com.auth.jsid.model.AuthenticationResponse;
import com.auth.jsid.model.CsrfTokenResponse;
import com.auth.jsid.model.ForgetPasswordData;
import com.auth.jsid.model.OTP;
import com.auth.jsid.model.OtpModel;
import com.auth.jsid.model.User;
import com.auth.jsid.userRepository.OtpRepository;
import com.auth.jsid.userRepository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    CsrfTokenRepository csrfTokenRepo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private SecurityContextRepository contexts;
    @Autowired
    private SessionAuthenticationStrategy sessions;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpRepository otpRepository;
    private static final String PENDING_EMAIL = "PENDING_EMAIL";
    private static final long OTP_VALIDITY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    
    
public CsrfTokenResponse generateCsrf(
        HttpServletRequest request,
        HttpServletResponse response) {

    CsrfToken csrfToken = csrfTokenRepo.loadToken(request);

    if (csrfToken == null) {
        csrfToken = csrfTokenRepo.generateToken(request);
        csrfTokenRepo.saveToken(csrfToken, request, response);
    }

    return new CsrfTokenResponse(
            csrfToken.getToken(),
            csrfToken.getHeaderName()
    );
}


public String ResgisterUser(User user) {

    if (user == null) {
        throw new IllegalArgumentException("User cannot be null");

    }

    User userDetails = User.builder()
            .email(user.getEmail())
            .password(passwordEncoder.encode(user.getPassword()))
            .username(user.getUsername())
            .role(user.getRole())
            .build();
    userRepository.save(userDetails);

    return "successfully Register the User";

}

    
public OtpModel generateOtp(User user) {
    String otp = "12345"; // Testing only

    OtpModel otpModel = otpRepository.findByUser(user)
            .orElseGet(() -> OtpModel.builder()
                    .user(user)
                    .build());

    otpModel.setUser(user);
    otpModel.setOtp(otp);
    otpModel.setExpiryDate(
            LocalDateTime.now().plusMinutes(5)
    );

    return otpRepository.save(otpModel);
}
    
    public String Signin(AuthenticationResponse data, HttpServletRequest request,
            HttpServletResponse response) {

        Authentication pendingAuthentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));

       
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println(user);
        generateOtp(user);

        // Password verification is only the first step. Do not put this authentication
        // in the SecurityContext until the OTP has also been verified.
        request.getSession(true).setAttribute(PENDING_EMAIL, user.getEmail());
        System.out.println(user.getEmail()+"checking for email");
        return "otp sent";
    }

    public String verifyOtp(OTP otp, HttpServletRequest request, HttpServletResponse response) {

        if (otp == null || otp.getEmail() == null || otp.getEmail().isBlank()
                || otp.getOtp() == null || otp.getOtp().isBlank()) {
            throw new IllegalArgumentException("Email and OTP are required");
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("No pending authentication found");
        }

        String pendingUsername = (String) session.getAttribute(PENDING_EMAIL);
        if (pendingUsername == null) {
            throw new IllegalStateException("No pending authentication found");
        }

        User user = userRepository.findByEmail(otp.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!pendingUsername.equals(user.getEmail())) {
            throw new IllegalArgumentException("OTP does not belong to the pending login");
        }

        OtpModel otpModel = otpRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("OTP not found"));
        if (otpModel.getExpiryDate() == null || LocalDateTime.now().isAfter(otpModel.getExpiryDate())) {
            otpRepository.delete(otpModel);
            System.out.println(otpModel.getExpiryDate()+"expiry date"+ LocalDateTime.now().isAfter(otpModel.getExpiryDate()));
            session.removeAttribute(PENDING_EMAIL);
            throw new IllegalArgumentException("OTP has expired");
        }
        if (!otp.getOtp().equals(otpModel.getOtp())) {
            throw new IllegalArgumentException("Wrong OTP");
        }

        Authentication completedAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        sessions.onAuthentication(completedAuthentication, request, response);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(completedAuthentication);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        session.removeAttribute(PENDING_EMAIL);
        otpRepository.delete(otpModel);

        return "Verification successful";
    }

    


    public String updatePassword(ForgetPasswordData data) {

        if (data == null || data.getEmail() == null || data.getEmail().isBlank()
                || data.getPassword() == null || data.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        userRepository.save(user);
        return "password updated successfully";
    }


    

}
