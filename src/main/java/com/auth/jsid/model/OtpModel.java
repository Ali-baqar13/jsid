package com.auth.jsid.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor 
public class OtpModel {


   

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private String otp;
    private LocalDateTime expiryDate;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    public OtpModel(Integer id, String otp, LocalDateTime expiryDate, User user) {
        
        this.id = id;
        this.otp = otp;
        this.expiryDate = expiryDate;
        this.user = user;   
    }
    
}
