package com.example.semestralkaa.repositories;

import com.example.semestralkaa.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer> {
    public Optional<ResetToken> getResetTokenByToken(String token);
}
