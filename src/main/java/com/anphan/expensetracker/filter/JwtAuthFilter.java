package com.anphan.expensetracker.filter;

import com.anphan.expensetracker.service.CustomUserDetailsService;
import com.anphan.expensetracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = đảm bảo filter chỉ chạy 1 lần per request

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Bước 1: Đọc header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Bước 2: Không có header hoặc không phải Bearer → bỏ qua, đi tiếp
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bước 3: Extract token (bỏ "Bearer " ở đầu)
        final String token = authHeader.substring(7);

        // Bước 4: Extract email từ token
        final String email = jwtService.extractEmail(token);

        // Bước 5: Nếu có email và chưa được xác thực trong session hiện tại
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Bước 6: Load UserDetails từ DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Bước 7: Verify token hợp lệ
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {

                // Bước 8: Tạo Authentication object và lưu vào SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // credentials = null vì đã verify qua token
                                userDetails.getAuthorities() // roles/permissions
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Bước 9: Set vào SecurityContextHolder
                // Từ đây các layer sau có thể gọi getAuthentication() để lấy user
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Bước 10: Đi tiếp đến filter/controller tiếp theo
        filterChain.doFilter(request, response);
    }
}