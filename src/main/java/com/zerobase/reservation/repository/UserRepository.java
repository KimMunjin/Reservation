package com.zerobase.reservation.repository;

import com.zerobase.reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Username으로 find
    Optional<User> findByUsername(String username);
    
    //username 존재 여부
    boolean existsByUsername(String username);

}
