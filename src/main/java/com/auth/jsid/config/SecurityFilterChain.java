package com.auth.jsid.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.auth.jsid.model.User;
import com.auth.jsid.userRepository.UserRepository;

@EnableWebSecurity
@Configuration 
public class SecurityFilterChain {

    @Bean
    public UserDetailsService userDetailsServices(UserRepository user) {
        return username -> user.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
    public CsrfTokenRepository  csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetails, PasswordEncoder encoder) {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetails);
        daoAuthenticationProvider.setPasswordEncoder(encoder);
        return new ProviderManager(daoAuthenticationProvider);

    }

    @Bean
    public SessionAuthenticationStrategy sessionAutheticationStrategy(CsrfTokenRepository  csrf)

    {
        return new CompositeSessionAuthenticationStrategy(
                List.of(new ChangeSessionIdAuthenticationStrategy(), new CsrfAuthenticationStrategy(csrf)));
    }

    @Bean
    public org.springframework.security.web.SecurityFilterChain filterChain(HttpSecurity http,
            CsrfTokenRepository  csrf, AuthenticationManager manager, SecurityContextRepository context)
            throws Exception {
        return http.authenticationManager(manager)
                .securityContext(contexts -> contexts.securityContextRepository(context))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .csrf(config -> config.csrfTokenRepository(csrf)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth","/auth/register", "/auth/login", "/auth/getCsrf", "/error").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> response.sendError(401))
                        .accessDeniedHandler((request, response, exception) -> response.sendError(403)))
                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .invalidateHttpSession(true).clearAuthentication(true)
                        .deleteCookies("SESSION", "JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204)))
                .build();

    }

}
