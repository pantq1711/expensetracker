package com.anphan.expensetracker.service;

import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.UnauthorizedException;
import com.anphan.expensetracker.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void validateAndRevokeToken_WhenTokenAlreadyRevoked_ShouldDeleteAllAndThrow() {
        // ARRANGE
        User user = new User();
        user.setId(1L);

        RefreshToken stolenToken = new RefreshToken();
        stolenToken.setToken("stolen-123");
        stolenToken.setUser(user);
        stolenToken.setRevoked(true); // Token đã được dùng từ trước

        when(refreshTokenRepository.findByToken("stolen-123")).thenReturn(Optional.of(stolenToken));

        // ACT & ASSERT
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> refreshTokenService.validateAndRevokeToken("stolen-123"));

        assertEquals("Suspicious activity detected: Refresh token reuse!", exception.getMessage());

        // Verify
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }

    @Test
    void validateToken_WhenTokenExpired_ShouldThrowException() {
        // ARRANGE
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setToken("expired-123");
        // Set ngày hết hạn là ngày hôm qua (Instant.now().minus...)
        expiredToken.setExpiryDate(java.time.Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS));

        when(refreshTokenRepository.findByToken("expired-123"))
                .thenReturn(java.util.Optional.of(expiredToken));

        // ACT & ASSERT
        com.anphan.expensetracker.exception.UnauthorizedException exception = assertThrows(
                com.anphan.expensetracker.exception.UnauthorizedException.class,
                () -> refreshTokenService.validateAndRevokeToken("expired-123")
        );

        assertEquals("Refresh token was expired.", exception.getMessage());

        // VERIFY: Token hết hạn thì phải bị xóa khỏi DB
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    void validateToken_WhenTokenValid_ShouldReturnTokenAndSetRevoked() {
        // ARRANGE
        RefreshToken validToken = new RefreshToken();
        validToken.setToken("valid-123");
        validToken.setRevoked(false);
        // Set ngày hết hạn là ngày mai
        validToken.setExpiryDate(java.time.Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS));

        when(refreshTokenRepository.findByToken("valid-123"))
                .thenReturn(java.util.Optional.of(validToken));

        // ACT
        RefreshToken result = refreshTokenService.validateAndRevokeToken("valid-123");

        // ASSERT
        assertNotNull(result);
        assertEquals("valid-123", result.getToken());

        // đánh dấu là đã dùng
        assertTrue(result.isRevoked());

        // VERIFY
        verify(refreshTokenRepository, times(1)).save(validToken);
    }

    @Test
    void createRefreshToken_ShouldSaveAndReturnNewToken() {
        // ARRANGE
        User user = new User();
        user.setId(1L);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        RefreshToken result = refreshTokenService.createRefreshToken(user);

        // ASSERT
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void validateAndRevokeToken_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        String notFoundToken = "invalid-token";

        when(refreshTokenRepository.findByToken(notFoundToken)).thenReturn(java.util.Optional.empty());

        // ACT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateAndRevokeToken(notFoundToken));

        // ASSERT
        assertEquals("Refresh token is not found", exception.getMessage());
        verify(refreshTokenRepository, never()).save(any());
    }

}