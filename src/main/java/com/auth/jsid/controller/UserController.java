package com.auth.jsid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.jsid.model.CsrfTokenResponse;
import com.auth.jsid.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class UserController {


    @Autowired 
    AuthService authService;

    @GetMapping("/getCsrf")
    public ResponseEntity<CsrfTokenResponse> csrfToken(HttpServletRequest request, HttpServletResponse response) {

        return new ResponseEntity<>(authService.generateCsrf(request, response), HttpStatus.CREATED);
        
    }
    
}
