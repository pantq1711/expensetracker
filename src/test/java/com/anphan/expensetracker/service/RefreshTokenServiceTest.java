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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        // Đảm bảo hệ thống đã xóa mọi token của user này
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}