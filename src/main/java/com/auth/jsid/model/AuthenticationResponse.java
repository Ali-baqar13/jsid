package com.auth.jsid.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
public class AuthenticationResponse {

    private String email;
    private String password;

    public AuthenticationResponse(String email, String password) {

        this.email = email;
        this.password = password;
        
    }
    
}
