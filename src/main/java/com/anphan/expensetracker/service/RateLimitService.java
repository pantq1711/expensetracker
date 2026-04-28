package com.anphan.expensetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${rate-limit.login.max-attempts}")
    private int maxAttempts;

    @Value(("${rate-limit.login.window-seconds}"))
    private long windowSeconds;

    private static final String KEY_PREFIX = "rate_limit:login:";

    public boolean isAllowed(String ip){
        String key = KEY_PREFIX + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if(count == null) return true; //Redis loi, tam thoi cho user di qua

        if(count == 1){
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        return count <= maxAttempts;
    }

    public void resetLimit(String ip) {
        redisTemplate.delete(KEY_PREFIX + ip);
    }
}
