package com.auth.jsid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.auth.jsid.model.AuthenticationResponse;
import com.auth.jsid.model.CsrfTokenResponse;
import com.auth.jsid.model.ForgetPasswordData;
import com.auth.jsid.model.OTP;
import com.auth.jsid.model.User;
import com.auth.jsid.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class UserController {


    @Autowired 
    AuthService authService;

    // @Autowired
    // GmailService gmailService;

    @GetMapping("/getCsrf")
    public ResponseEntity<CsrfTokenResponse> csrfToken(HttpServletRequest request, HttpServletResponse response) {

        return new ResponseEntity<>(authService.generateCsrf(request, response), HttpStatus.CREATED);
        
    }

    @PostMapping("/register")
    public ResponseEntity<String> login(@RequestBody User user){

        return new ResponseEntity<>(authService.ResgisterUser(user), HttpStatus.CREATED);

    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationResponse data, HttpServletRequest req, HttpServletResponse res){

        return new ResponseEntity<>(authService.Signin(data,req, res), HttpStatus.OK);

    }
    // @PostMapping 
    // public ResponseEntity<String> verifyOtp(@RequestBody OTP otpData, HttpServletRequest request,
    //         HttpServletResponse response) {

    //      return new ResponseEntity<>(authService.verifyOtp(otpData, request, response),HttpStatus.CREATED);

    // }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody ForgetPasswordData data) {
        return new ResponseEntity<>(authService.updatePassword(data), HttpStatus.OK);

    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody OTP otp,HttpServletRequest req, HttpServletResponse res){

        return ResponseEntity.ok(authService.verifyOtp(otp, req, res));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidOtp(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleMissingLoginSession(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
    // @GetMapping("/sendOtp")
    // public ResponseEntity<String> sendOtp(@RequestBody String email) {
    //     try {
    //         gmailService.sendOtpAdmin(email);
    //         return new ResponseEntity<>("OTP senr successfully", HttpStatus.OK);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(500).body(e.getMessage());
    //     }
        
    // } 
    
}
