package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryReportDTO>> getSummaryByCategory(){
        return ResponseEntity.ok(reportService.getSumByCategoryName());
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryProjection> getSummary(){
        return ResponseEntity.ok(reportService.getSummary());
    }

}
