package com.example.demo.Service;

import com.example.demo.Entity.PasswordReset;
import com.example.demo.Repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;

    @Autowired
    public PasswordResetService(PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    public void save(PasswordReset passwordReset) {
        passwordResetRepository.save(passwordReset);
    }

    public PasswordReset findByToken(String token) {
        return passwordResetRepository.findByMaToken(token);
    }
}
