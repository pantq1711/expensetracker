package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RefreshTokenRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user registration, login, and token management")
@RequiredArgsConstructor
@Builder
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    @Operation(summary = "Register a new account", description = "Creates a new user and returns initial access/refresh tokens")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @Operation(summary = "Login to the system", description = "Authenticates user credentials and returns JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        return ResponseEntity.ok(authService.login(request, ip));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Operation(summary = "Refresh Access Token", description = "Generates a new access token using a valid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @Operation(summary = "Logout", description = "Invalidates the current user's refresh token from the database")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        log.info("=> Token bóc ra để chuẩn bị blacklist: [{}]", token);
        authService.logout(securityUtils.getCurrentUser(), token);
        return ResponseEntity.noContent().build();
    }
}