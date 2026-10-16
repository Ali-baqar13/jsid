package com.auth.jsid.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
public class AuthenticationResponse {

    private String username;
    private String password;

    public AuthenticationResponse(String username, String password) {

        this.username = username;
        this.password = password;
        
    }
    
}
