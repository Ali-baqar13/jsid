package com.auth.jsid.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.auth.jsid.model.User;
import com.auth.jsid.userRepository.UserRepository;

@EnableWebSecurity 
public class SecurityFilterChain {

    


    @Bean
    public UserDetailsService userDetailsServices(UserRepository user) {
        return username->user.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"))
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean 
    public HttpSessionCsrfTokenRepository csrfTokenRepository() { return new HttpSessionCsrfTokenRepository(); }

    @Bean 
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(UserDetailsService userDetails);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
        
    }
    @Bean
    public SessionAuthenticationStrategy sessionAutheticationStrategy(HttpSessionCsrfTokenRepository csrf)
   
    {
         return new CompositeSessionAuthenticationStrategy(List.of(new ChangeSessionIdAuthenticationStrategy(), new CsrfAuthenticationStrategy(csrf)))
    }
    
}
