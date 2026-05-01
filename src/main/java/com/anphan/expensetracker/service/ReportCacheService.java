package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TTL = Duration.ofMinutes(30);
    private static final String SUMMARY_PREFIX = "report:summary:";
    private static final String CATEGORY_PREFIX = "report:category:";

    //summary

    public void cacheSummary(Long userId, SummaryProjection data) {
        try {
            String key = SUMMARY_PREFIX + userId;
            // SummaryProjection là interface -> cần convert sang Map trước khi serialize
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.info("[REPORT-CACHE] Cached summary for userId=[{}]", userId);
        } catch (Exception e) {
            log.error("[REPORT-CACHE] Failed to cache summary: {}", e.getMessage(), e);
        }
    }

    public String getCachedSummary(Long userId) {
        return redisTemplate.opsForValue().get(SUMMARY_PREFIX + userId);
    }

    //category

    public void cacheCategory(Long userId, List<CategoryReportDTO> data) {
        try {
            String key = CATEGORY_PREFIX + userId;
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.info("[REPORT-CACHE] Cached category report for userId=[{}]", userId);
        } catch (Exception e) {
            log.error("[REPORT-CACHE] Failed to cache category: {}", e.getMessage(), e);
        }
    }

    public List<CategoryReportDTO> getCachedCategory(Long userId) {
        try {
            String json = redisTemplate.opsForValue().get(CATEGORY_PREFIX + userId);
            if (json == null) return null;
            return objectMapper.readValue(json, new TypeReference<List<CategoryReportDTO>>() {});
        } catch (Exception e) {
            log.error("[REPORT-CACHE] Failed to read category cache: {}", e.getMessage(), e);
            return null;
        }
    }

    public void invalidateUserReports(Long userId) {
        redisTemplate.delete(SUMMARY_PREFIX + userId);
        redisTemplate.delete(CATEGORY_PREFIX + userId);
        log.info("[REPORT-CACHE] Invalidated all reports for userId=[{}]", userId);
    }
}