package com.auth.jsid.model;

import lombok.Data;

@Data 
public class OTP {

    private String email;
    private String otp;

    public OTP(String email, String otp){
        this.email= email;
        this.otp = otp;

    }


    
}
