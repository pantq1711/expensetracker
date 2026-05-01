package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.dto.SummaryProjectionImpl;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private com.anphan.expensetracker.util.SecurityUtils securityUtils;

    @Mock
    private ReportCacheService reportCacheService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ReportService reportService;

    private User mockUser;
    private SummaryProjectionImpl mockSummary;

    @BeforeEach
    void setUp() {
        // Chuẩn bị Mock User
        mockUser = new User();
        mockUser.setId(1L);

        // Chuẩn bị Mock Summary Data
        mockSummary = new SummaryProjectionImpl();
        mockSummary.setTotalIncome(new BigDecimal("5000000"));
        mockSummary.setTotalExpense(new BigDecimal("2000000"));
    }

    @Test
    void getSumByUser_CacheMiss_ShouldQueryDBAndCacheResult() {
        // Arrange
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(reportCacheService.getCachedSummary(mockUser.getId())).thenReturn(null); // Giả lập Cache MISS
        when(transactionRepository.sumByUser(mockUser)).thenReturn(mockSummary);

        // Act
        SummaryProjection result = reportService.getSumByUser();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5000000"), result.getTotalIncome());
        verify(transactionRepository, times(1)).sumByUser(mockUser);
        verify(reportCacheService, times(1)).cacheSummary(eq(mockUser.getId()), eq(mockSummary));
    }

    @Test
    void getSumByUser_CacheHit_ShouldReturnFromCacheAndNotQueryDB() throws Exception {
        // Arrange
        String cachedJson = "{\"totalIncome\":5000000,\"totalExpense\":2000000}";

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(reportCacheService.getCachedSummary(mockUser.getId())).thenReturn(cachedJson); // Giả lập Cache HIT
        when(objectMapper.readValue(cachedJson, SummaryProjectionImpl.class)).thenReturn(mockSummary);

        // Act
        SummaryProjection result = reportService.getSumByUser();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5000000"), result.getTotalIncome());
        verify(transactionRepository, never()).sumByUser(any(User.class));
        verify(reportCacheService, never()).cacheSummary(anyLong(), any());
    }
}