package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.AuthResponse;
import com.anphan.expensetracker.dto.LoginRequest;
import com.anphan.expensetracker.dto.RegisterRequest;
import com.anphan.expensetracker.dto.UserDTO;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
// Thêm import này vào AuthService
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // hash password

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists!");
        }
        else {
            User user = User.builder().name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
        }
        AuthResponse response = new AuthResponse();
        response.setToken("dummy-token");
        return response;

    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found!"));
            // check password
            if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            {
                throw new RuntimeException("Wrong password!");
            }
            AuthResponse response = new AuthResponse();
            response.setToken("dummy-token");
            return response;
    }
}
