package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.FilterReportProjection;
import com.anphan.expensetracker.dto.RealDashBoardProjection;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    User user = securityUtils.getCurrentUser();

    // Bài 3: Tổng quan Thu/Chi
    public SummaryProjection getSumByUser() {
        return transactionRepository.sumByUser(user);
    }

    // Bài 6 (Cũ): Thống kê theo Category
    public List<CategoryReportDTO> getReportByNameCategory() {
        return transactionRepository.sumByCategoryName(user);
    }

    // --- BỔ SUNG BÀI 4: Filter & Report ---
    public FilterReportProjection getTotalAndCount(LocalDate from, LocalDate to, Transaction.TransactionType type) {
        return transactionRepository.totalAndCountBetweenDate(user, from, to, type);
    }

    // --- BỔ SUNG BÀI 5: So sánh 2 tháng ---
    public RealDashBoardProjection getDiff(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        return transactionRepository.difBetweenMonth(user, s1, e1, s2, e2);
    }
}