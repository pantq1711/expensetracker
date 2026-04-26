package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User mockUser; //

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("anphan@example.com");
        loginRequest.setPassword("password123");
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("anphan@example.com");
        mockUser.setPassword("encoded-password");
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnTokens() {
        // ARRANGE
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(java.util.Optional.of(mockUser));

        RefreshToken mockRt = new RefreshToken();
        mockRt.setToken("RT1");
        when(refreshTokenService.createRefreshToken(mockUser)).thenReturn(mockRt);
        when(jwtService.generateToken(mockUser.getEmail())).thenReturn("AT1");

        // ACT
        AuthResponse result = authService.login(loginRequest);

        // ASSERT
        assertNotNull(result);
        assertEquals("AT1", result.getToken());
        assertEquals("RT1", result.getRefreshToken());
    }
    @Test
    void login_WhenWrongPassword_ShouldThrowBadCredentialsException() {
        // ARRANGE
        // AuthenticationManager ném lỗi
        when(authenticationManager.authenticate(any(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // ACT & ASSERT
        org.springframework.security.authentication.BadCredentialsException exception = assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Bad credentials", exception.getMessage());

        // VERIFY: Đảm bảo không sinh token rác
        verify(refreshTokenService, never()).createRefreshToken(any());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setEmail("anphan@example.com");
        request.setName("An Phan");
        request.setPassword("password123");

        // Dặn Repo báo email đã tồn tại
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("Email already exists!", exception.getMessage());

        // VERIFY: Tuyệt đối không gọi save()
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void refreshToken_WhenDetectReuse_ShouldReject() {
        // ARRANGE
        String stolenToken = "day-la-token-bi-danh-cap";

        //
        when(refreshTokenService.validateAndRevokeToken(stolenToken))
                .thenThrow(new com.anphan.expensetracker.exception.UnauthorizedException("Suspicious activity detected: Refresh token reuse!"));

        // ACT & ASSERT
        com.anphan.expensetracker.exception.UnauthorizedException exception = assertThrows(
                com.anphan.expensetracker.exception.UnauthorizedException.class,
                () -> authService.refreshToken(stolenToken));

        assertEquals("Suspicious activity detected: Refresh token reuse!", exception.getMessage());

        // VERIFY: Không cấp Access Token mới
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void register_WhenValidRequest_ShouldSaveAndReturnTokens() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setEmail("anphan@example.com");
        request.setName("An Phan");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setEmail("anphan@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RefreshToken mockRt = new RefreshToken();
        mockRt.setToken("RT1");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(mockRt);
        when(jwtService.generateToken(anyString())).thenReturn("AT1");

        // ACT
        AuthResponse result = authService.register(request);

        // ASSERT
        assertNotNull(result);
        assertEquals("AT1", result.getToken());
        assertEquals("RT1", result.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void refreshToken_WhenValidToken_ShouldReturnNewTokens() {
        // ARRANGE
        String oldToken = "valid-old-refresh-token";

        User user = new User();
        user.setEmail("anphan@com");

        RefreshToken validatedToken = new RefreshToken();
        validatedToken.setUser(user);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("RT1");

        when(refreshTokenService.validateAndRevokeToken(oldToken)).thenReturn(validatedToken);
        when(jwtService.generateToken(user.getEmail())).thenReturn("AT1");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(newRefreshToken);

        // ACT
        AuthResponse result = authService.refreshToken(oldToken);

        // ASSERT
        assertNotNull(result);
        assertEquals("AT1", result.getToken());
        assertEquals("RT1", result.getRefreshToken());
    }
}