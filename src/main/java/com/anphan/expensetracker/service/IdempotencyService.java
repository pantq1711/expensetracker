package com.anphan.expensetracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    public Optional<String> getCachedResponse(String key) {
        String cached = redisTemplate.opsForValue().get(PREFIX + key);
        if (cached != null) {
            log.info("[IDEMPOTENCY] Cache HIT key=[{}]", key);
        }
        return Optional.ofNullable(cached);
    }

    public void cacheResponse(String key, Object response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(PREFIX + key, json, TTL);
            log.info("[IDEMPOTENCY] Cached response key=[{}]", key);
        } catch (Exception e) {
            log.error("[IDEMPOTENCY] Failed to cache: {}", e.getMessage());
        }
    }
}