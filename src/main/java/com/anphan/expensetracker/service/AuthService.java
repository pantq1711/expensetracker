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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // inject JwtService để tạo token
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

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

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshTokenStr){
        RefreshToken oldRefreshToken = refreshTokenService.verifyRefreshToken(refreshTokenStr);
        User user = oldRefreshToken.getUser();
        return buildAuthResponse(user);
    }

    public void logout(User user){
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