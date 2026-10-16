package com.auth.jsid.userRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.jsid.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User,Integer>(String username) {
    
    Optional<User>  findByUsername(username);
    
}
