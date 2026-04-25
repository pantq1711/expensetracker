package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.exception.UnauthorizedException;
import com.anphan.expensetracker.repository.RefreshTokenRepository;
import com.anphan.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    public RefreshToken createRefreshToken(User user){

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(86400000L * 7))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validateAndRevokeToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is not found"));

        // 1. REUSE DETECTION
        if(refreshToken.isRevoked()){
            refreshTokenRepository.deleteByUser(refreshToken.getUser());
            throw new UnauthorizedException("Suspicious activity detected: Refresh token reuse!");
        }

        // 2. Check Expiry
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token was expired.");
        }

        // 3. Token hợp lệ -> Đánh dấu là đã sử dụng (Revoke) để không ai được dùng lại cái này nữa
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public void deleteRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
