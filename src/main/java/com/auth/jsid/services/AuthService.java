package com.auth.jsid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.auth.jsid.model.otpModel;
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

    // public CsrfTokenResponse generateCsrf(HttpServletRequest request, HttpServletResponse response) {
    //     CsrfToken csrfToken = csrfTokenRepo.loadToken(request);
    //     if (csrfToken == null) {
    //         csrfToken = csrfTokenRepo.loadToken(request);
    //         csrfTokenRepo.saveToken(csrfToken, request, response);

    //     }
    //     return new CsrfTokenResponse(csrfToken.getToken(), csrfToken.getHeaderName());
    // }
    
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

    public Authentication Signin(AuthenticationResponse data, HttpServletRequest request,
            HttpServletResponse response) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));
        sessionAuthenticationStrategy.onAuthentication(
                auth,
                request,
                response);
        OtpModel Otp = OtpModel().bui        
        
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


    public String verifyOtp(OTP otpData) {

        String hardCodeOtp = "9797";
        if(otpData.getOtp() == null || otpData.getOtp().isBlank()){
            throw new IllegalArgumentException("OTP IS required");
        }

        if(!hardCodeOtp.equals(otpData.getOtp())){
          throw new IllegalArgumentException("wrong Otp");
        }

       return "verification succesfull";
    }

}
