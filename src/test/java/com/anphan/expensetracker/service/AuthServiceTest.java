package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("anphan@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnTokens() {
        // TODO
    }

    @Test
    void login_WhenWrongPassword_ShouldThrowBadCredentialsException() {
        // TODO
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // TODO
    }

    @Test
    void login_WhenDetectReuse_ShouldReject() {
        // TODO
    }
}