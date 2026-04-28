package com.anphan.expensetracker.filter;

import com.anphan.expensetracker.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    private static final String LOGIN_PATH = "/api/auth/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Chỉ áp dụng cho login endpoint
        if (!LOGIN_PATH.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);

        if (!rateLimitService.isAllowed(ip)) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            response.setContentType("application/json");
            response.setStatus(429); // Too Many Requests

            Map<String, Object> body = Map.of(
                    "status", 429,
                    "error", "Too Many Requests",
                    "message", "Too many attempts. Try again in 60 seconds."
            );

            new ObjectMapper().writeValue(response.getOutputStream(), body);
            return;
        }

        filterChain.doFilter(request, response);
}

    // Lấy IP thật của client, xét cả trường hợp đứng sau proxy/load balancer
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim(); // Lấy IP đầu tiên trong chain
        }
        return request.getRemoteAddr();
    }
}

