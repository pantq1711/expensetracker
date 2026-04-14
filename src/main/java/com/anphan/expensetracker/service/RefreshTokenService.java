package com.anphan.expensetracker.service;

import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.exception.UnauthorizedException;
import com.anphan.expensetracker.repository.RefreshTokenRepository;
import com.anphan.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new UnauthorizedException("Khong tim thay RefreshToken"));

        if(refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("RefreshToken da het han");
        }
        return refreshToken;
    }

    public void deleteRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
