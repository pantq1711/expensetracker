package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(com.anphan.expensetracker.entity.User user);
}
