package com.auth.jsid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor 
public class CsrfTokenResponse {

    private String token;
    private String headerName;

    public CsrfTokenResponse(String token, String headerName){
        this.token = token;
        this.headerName = headerName;
    }
    
}
