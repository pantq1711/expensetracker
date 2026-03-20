package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public List<CategoryReportDTO> getSumByCategoryName(){
        User user = new User();
        user.setId(2L);
        return transactionRepository.sumByCategoryName(user);
    }

    public SummaryProjection getSummary(){
        User user = new User();
        user.setId(2L);
        return transactionRepository.getSummary(user);
    }

}
