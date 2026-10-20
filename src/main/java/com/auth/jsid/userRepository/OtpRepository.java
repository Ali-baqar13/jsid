package com.auth.jsid.userRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.jsid.model.OtpModel;

public interface OtpRepository extends JpaRepository<OtpModel, Integer> {

    
    
}
