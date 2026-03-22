package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.FilterReportProjection;
import com.anphan.expensetracker.dto.RealDashBoardProjection;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.service.ReportService;
import com.anphan.expensetracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    // Xóa TransactionService nếu không dùng đến trong controller này để code sạch hơn

    // Bài 3: Tổng quan (Income/Expense)
    @GetMapping("/summary")
    public ResponseEntity<SummaryProjection> getSummary() {
        return ResponseEntity.ok(reportService.getSumByUser());
    }

    // Bài 6: Thống kê theo Category
    @GetMapping("/category")
    public ResponseEntity<List<CategoryReportDTO>> getSummaryByCategoryName() {
        return ResponseEntity.ok(reportService.getReportByNameCategory());
    }

    // --- BÀI 4: Filter & Report ---
    @GetMapping("/filter")
    public ResponseEntity<FilterReportProjection> getTotalAndCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam Transaction.TransactionType type) {

        // SỬA LỖI TẠI ĐÂY: Gọi đúng method từ ReportService
        return ResponseEntity.ok(reportService.getTotalAndCount(from, to, type));
    }

    // --- BÀI 5: So sánh 2 tháng ---
    @GetMapping("/compare")
    public ResponseEntity<RealDashBoardProjection> getCompare(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end2) {

        return ResponseEntity.ok(reportService.getDiff(start1, end1, start2, end2));
    }
}