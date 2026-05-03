package com.anphan.expensetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final StringRedisTemplate redisTemplate;

    private final JwtService jwtService;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blackListToken(String token){
        Date expiration = jwtService.extractExpiration(token);
        long TTLMils = expiration.getTime() - System.currentTimeMillis();
        log.info("=> TTL for token: expiration={} | now={} | ttlMillis={}", expiration.getTime(), System.currentTimeMillis(), TTLMils);
        if (TTLMils <= 0) {
            log.warn("=>Token is expired, Blacklist is not needed!");
            return;
        }

        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "revoked",
                    Duration.ofMillis(TTLMils)
        );
        Boolean isSaved = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
        log.info("=> Have saved into Redis successfully? {}", isSaved);
    }

    public boolean isBlackListed(String token){
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
