package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.FilterReportProjection;
import com.anphan.expensetracker.dto.RealDashBoardProjection;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reports & Analytics", description = "APIs for retrieving aggregated data for charts and dashboards")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get summary of Income/Expense and Balance", description = "Calculates the total income, total expense, and current balance for the user.")
    @GetMapping("/summary")
    public ResponseEntity<SummaryProjection> getSummary() {
        return ResponseEntity.ok(reportService.getSumByUser());
    }

    @Operation(summary = "Get total amount grouped by category")
    @GetMapping("/category")
    public ResponseEntity<List<CategoryReportDTO>> getSummaryByCategoryName() {
        return ResponseEntity.ok(reportService.getReportByNameCategory());
    }

    @Operation(summary = "Get total amount and transaction count within a date range")
    @GetMapping("/filter")
    public ResponseEntity<FilterReportProjection> getTotalAndCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam Transaction.TransactionType type) {
        return ResponseEntity.ok(reportService.getTotalAndCount(from, to, type));
    }

    @Operation(summary = "Compare totals between two date ranges", description = "Useful for comparing current month vs. previous month statistics.")
    @GetMapping("/compare")
    public ResponseEntity<RealDashBoardProjection> getCompare(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end2) {
        return ResponseEntity.ok(reportService.getDiff(start1, end1, start2, end2));
    }
}