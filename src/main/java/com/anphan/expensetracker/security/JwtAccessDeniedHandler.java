package com.anphan.expensetracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException{

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map <String, String> body = Map.of(
                "status", "403",
                "error", "Forbidden",
                "message", "Ban khong co quyen thuc hien hanh dong nay"
        );

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}