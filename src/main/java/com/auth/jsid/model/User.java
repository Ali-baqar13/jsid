package com.auth.jsid.model;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;







@Entity
@Data
@NoArgsConstructor
@Builder 
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Role role;

    public User(Integer id, String username, String password, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return List.of();

        }
        return List.of(new SimpleGrantedAuthority(role.name().toUpperCase(Locale.ROOT)));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
