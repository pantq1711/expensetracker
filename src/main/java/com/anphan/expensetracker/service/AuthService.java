package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.entity.RefreshToken;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.RefreshTokenRepository;
import com.anphan.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // inject JwtService để tạo token
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RateLimitService rateLimitService;
    private final TokenBlackListService tokenBlackListService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request, String ip) {
        //dung framework thay vi check password, email thu cong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Login thành công -> xóa counter, tránh block user hợp lệ
        rateLimitService.resetLimit(ip);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshTokenStr){
        RefreshToken oldRefreshToken = refreshTokenService.validateAndRevokeToken(refreshTokenStr);
        User user = oldRefreshToken.getUser();
        return buildAuthResponse(user);
    }

    public void logout(User user, String accessToken){
        tokenBlackListService.blackListToken(accessToken);
        refreshTokenService.deleteRefreshToken(user);
    }

    public AuthResponse buildAuthResponse(User user){
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtService.generateToken(user.getEmail());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setRefreshToken(refreshToken.getToken());
        authResponse.setToken(accessToken);
        return authResponse;
    }
}