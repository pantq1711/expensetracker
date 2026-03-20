package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByUser(User user);

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end, Pageable pageable);

    Page<Transaction> findByUserAndAmountGreaterThan(User user, BigDecimal amount, Pageable pageable);

    Page<Transaction> findByUserAndNoteContainingIgnoreCase (User user, String note, Pageable pageable);

    @Query("SELECT new com.anphan.expensetracker.dto.CategoryReportDTO(t.category.name, SUM (t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.user = :user " +
            "GROUP BY t.category.name")
    List<CategoryReportDTO> sumByCategoryName(@Param("user") User user);

    @Query("SELECT " +
            "COALESCE( SUM (CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome, "+
            "COALESCE( SUM (CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense "+
            "FROM Transaction t " +
            "WHERE t.user = :user")
    SummaryProjection getSummary(@Param("user") User user);

 }
