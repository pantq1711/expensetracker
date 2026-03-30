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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = đảm bảo filter chỉ chạy 1 lần per request
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String threadName = Thread.currentThread().getName(); // Lấy tên Thread hiện tại
        System.out.println("\n=== JwtAuthFilter === URL: " + request.getRequestURI());

        Object initialAuth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("--- [THREAD: " + threadName + "] ---");
        System.out.println("1. Auth ban đầu trong Thread: " + (initialAuth != null ? initialAuth : "TRỐNG"));

        final String header = request.getHeader("Authorization");
        System.out.println("Header: " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println(">>> Không có token → đi tiếp");
            filterChain.doFilter(request, response);
            return;
        }

        final String authToken = header.substring(7);
        try {
            final String email = jwtService.extractEmail(authToken);
            System.out.println("Email extract: " + email);
            System.out.println("Auth hiện tại: " + SecurityContextHolder.getContext().getAuthentication());

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtService.isTokenValid(authToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println(">>> Set auth thành công: " + userDetails.getUsername());
                }
            }
        } catch (Exception e) {
            System.out.println("2. Auth TRƯỚC khi clear: " + SecurityContextHolder.getContext().getAuthentication());
            System.out.println(">>> Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            SecurityContextHolder.clearContext();
            System.out.println("3. Auth SAU khi clear: " + SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);
    }
}