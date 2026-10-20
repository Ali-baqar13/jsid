package com.auth.jsid.userRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.jsid.model.OtpModel;
import com.auth.jsid.model.User;

public interface OtpRepository extends JpaRepository<OtpModel, Integer> {

    Optional<OtpModel> findByUser(User user);
    
}
