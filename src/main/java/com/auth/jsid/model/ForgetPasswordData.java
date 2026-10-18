package com.auth.jsid.model;

import lombok.Data;
import lombok.NoArgsConstructor;

 

@Data 
public class ForgetPasswordData {

    private String password;
    private String email;

    public ForgetPasswordData(String password, String email){
        this.email = email;
        this.password= password;


    }
    
}
