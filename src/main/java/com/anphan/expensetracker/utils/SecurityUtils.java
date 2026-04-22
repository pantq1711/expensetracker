package com.anphan.expensetracker.util;

import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    // Gọi method này ở bất kỳ Service nào thay cho hardcoded User(id=2L)
    public User getCurrentUser() {
        // Lấy Authentication object từ SecurityContext
        // Đây là object được JwtAuthFilter set vào ở Bước 9
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Principal là UserDetails object chứa email
        String email = ((UserDetails) principal).getUsername();

        // Load User thật từ DB theo email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isAdminOrOwner(Long resourceOwnerId) {
        User current = getCurrentUser();
        return current.getRole() == User.Role.ADMIN
                || current.getId().equals(resourceOwnerId);
    }
}