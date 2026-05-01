package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.*;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;
    private final ReportCacheService reportCacheService;
    private final ObjectMapper objectMapper;

    public SummaryProjection getSumByUser() {
        User user = getCurrentUser();

        // Check cache
        String cached = reportCacheService.getCachedSummary(user.getId());
        if (cached != null) {
            log.info("[REPORT] Cache HIT summary userId=[{}]", user.getId());
            try {
                // SummaryProjection là interface, cần dùng Map để deserialize
                return objectMapper.readValue(cached, SummaryProjectionImpl.class);
            } catch (Exception e) {
                log.warn("[REPORT] Cache read failed, fallback to DB", e);
            }
        }

        // Cache MISS -> query DB
        log.info("[REPORT] Cache MISS summary userId=[{}]", user.getId());
        SummaryProjection result = transactionRepository.sumByUser(user);
        reportCacheService.cacheSummary(user.getId(), result);
        return result;
    }

    public List<CategoryReportDTO> getReportByNameCategory() {
        User user = getCurrentUser();

        List<CategoryReportDTO> cached = reportCacheService.getCachedCategory(user.getId());
        if (cached != null) {
            log.info("[REPORT] Cache HIT category userId=[{}]", user.getId());
            return cached;
        }

        log.info("[REPORT] Cache MISS category userId=[{}]", user.getId());
        List<CategoryReportDTO> result = transactionRepository.sumByCategoryName(user);
        reportCacheService.cacheCategory(user.getId(), result);
        return result;
    }

    // filter và compare không cache vì params thay đổi liên tục
    public FilterReportProjection getTotalAndCount(LocalDate from, LocalDate to, Transaction.TransactionType type) {
        return transactionRepository.totalAndCountBetweenDate(getCurrentUser(), from, to, type);
    }

    public RealDashBoardProjection getDiff(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        return transactionRepository.difBetweenMonth(getCurrentUser(), s1, e1, s2, e2);
    }

    private User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }
}