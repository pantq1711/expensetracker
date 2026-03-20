package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.Budget;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    public List<Budget> findByUser(User user);

    public Page<Budget> findByUser(User user, Pageable pageable);

    Page<Budget> findByAmount(BigDecimal amount, Pageable pageable);

}
