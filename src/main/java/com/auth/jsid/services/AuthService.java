package com.auth.jsid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

import com.auth.jsid.model.AuthenticationResponse;
import com.auth.jsid.model.CsrfTokenResponse;
import com.auth.jsid.model.User;
import com.auth.jsid.userRepository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service 
public class AuthService {


    @Autowired
    CsrfTokenRepository csrfTokenRepo;
    @Autowired
    AuthenticationManager authenticatioManager;


    @Autowired
    private final SecurityContextRepository contexts;
    @Autowired
    private final SessionAuthenticationStrategy sessions;

    @Autowired
    PasswodEncoder passwordEncoder;

    @Autowired

    UserRepository userRepository;
    
    

    public CsrfTokenResponse generateCsrf(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = CsrfToken.loadToken(request);
        if (csrfToken == null) {
            csrfToken = CsrfToken.loadToken(request);
            csrfTokenRepo.saveToken(csrfToken, request, response);

        }
        return new CsrfTokenResponse(csrfToken.getToken(), csrfToken.getHeaderName());
    }

    public ResponseEntity<String> ResgisterUser(User user) {

        if (user == null) {
            
        }

        User user = new User().builder()
        .email(user.getEmail())
        .password(passwordEncoder(user.getPassword()))
        .username(user.getUsername())
                .build();
        userRepository.save(user);

        return new ResponseEntity<>("successfully Register the User", HttpStatus.CREATED);
                
        



        
        
    }
    
    public Authentication Signin(AuthenticationResponse data, HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));
        sessions.onAuthentication(auth, request, response);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        return auth;
        

        
    }
    
}
