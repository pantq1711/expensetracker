package com.anphan.expensetracker.service;

import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.exception.UnauthorizedException;
import com.anphan.expensetracker.repository.RefreshTokenRepository;
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

    //Tao RT moi cho user
    public RefreshToken createRefreshToken(User user){
        //Xoa token cu neu co - 1 user chi co 1 RT
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString()) // random UUID, không phải JWT
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Khong tim thay refreshToken"));

        if(refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("RefreshToken da het han, vui long dang nhap lai");
        }

        return refreshToken;
    }

    //Xoa refresh token khi logout
    public void deleteRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
