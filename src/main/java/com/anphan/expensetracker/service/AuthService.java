package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // inject JwtService để tạo token

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

        // Tạo JWT thật từ email
        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token); // trả JWT thật, không còn dummy-token
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }

        // Tạo JWT thật từ email
        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }
}