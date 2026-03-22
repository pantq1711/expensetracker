package com.anphan.expensetracker.repository;
import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.FilterReportProjection;
import com.anphan.expensetracker.dto.RealDashBoardProjection;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public List<Transaction> findByUser(User user);

    public Page<Transaction> findByUser(User user, Pageable pageable);

    public Page<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end, Pageable pageable);

    public Page<Transaction> findByUserAndType(User user, Transaction.TransactionType transactionType, Pageable pageable);


    @Query("""
       SELECT 
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS totalIncome,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS totalExpense
       FROM Transaction t
       WHERE t.user = :user
""")
    SummaryProjection sumByUser(@Param("user") User user);

    @Query("SELECT new com.anphan.expensetracker.dto.CategoryReportDTO(t.category.name, COALESCE(SUM(t.amount), 0.0)) "+
            "FROM Transaction t "+
            "WHERE t.user = :user " +
            "GROUP BY t.category.name")
    List<CategoryReportDTO> sumByCategoryName(@Param("user") User user);

    @Query("""
        SELECT COALESCE(SUM(t.amount)) AS total, COUNT(t) AS count
        FROM Transaction t
        WHERE t.user = :user 
        AND t.date BETWEEN :from AND :to
        AND t.type = :type
""")
    FilterReportProjection totalAndCountBetweenDate(@Param("user") User user,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to,
                                            @Param("type") Transaction.TransactionType transactionType);

    @Query("""
        SELECT 
        COALESCE(SUM(CASE WHEN t.date BETWEEN :start1 AND :end1 THEN t.amount ELSE 0 END), 0) AS amountMonth1,
        COALESCE(SUM(CASE WHEN t.date BETWEEN :start2 AND :end2 THEN t.amount ELSE 0 END), 0) AS amountMonth2
        FROM Transaction t
        WHERE t.user = :user  
""")
    RealDashBoardProjection difBetweenMonth(@Param("user") User user,
                                    @Param("start1") LocalDate start1,
                                    @Param("end1") LocalDate end1,
                                    @Param("start2") LocalDate start2,
                                    @Param("end2") LocalDate end2);

}

