package com.example.demo.Repository;

import com.example.demo.Entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    // Optional<PasswordReset> findByMaToken(String maToken);
    PasswordReset findByMaToken(String maToken);

}