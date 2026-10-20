package com.auth.jsid.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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

@Service
public class AuthService {

    @Autowired
    CsrfTokenRepository csrfTokenRepo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired 
    SessionAuthenticationStrategy sessionAuthenticationStrategy;
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
    String otp = String.format("%5d", new Random().nextInt(100000));

    LocalDateTime otpTimestamp = LocalDateTime.now();
    OtpModel otpModel = OtpModel.builder()
            .user(user)
            .otp(otp)
            .expiryDate(otpTimestamp)
            .build();
    otpRepository.save(otpModel);

    return otpModel;
}
    
    public Authentication Signin(AuthenticationResponse data, HttpServletRequest request,
            HttpServletResponse response) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));

        // sessionAuthenticationStrategy.onAuthentication(
        //         auth,
        //         request,
        //         response);
        User user = userRepository.findByUsername(data.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        generateOtp(user);    
        
        sessions.onAuthentication(auth, request, response);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        return auth;

    }

    public String   sendOTP(){

        return "OTP Send Successfully";
    }


    public String updatePassword(ForgetPasswordData data) {

        User user = userRepository.findByEmail(data.getEmail()).get();
        user.setPassword(data.getPassword());
        userRepository.save(user);
        return "password updated successfully";
    }


    public String verifyOtp(OTP otp) {

        // String hardCodeOtp = "9797";
        if (otp == null || otp.getEmail().isBlank()) {
            throw new IllegalArgumentException("OTP IS required");
        }
        
        Optional<User> user = userRepository.findByEmail(otp.getEmail()).orElseThrow(()-> IllegalArgumentException("User not found"));
      
         OtpModel otpModel = user.getOtpModel();
        if(!otp.equals(otpModel.getOtp())){
          throw new IllegalArgumentException("wrong Otp");
        }

       return "verification succesfull";
    }

}
