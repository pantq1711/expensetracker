package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByUser(User user);

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end, Pageable pageable);

    Page<Transaction> findByUserAndAmountGreaterThan (User user, BigDecimal bigDecimal, Pageable pageable);

    Page<Transaction> findByUserAndNoteContainingIgnoreCase (User user, String note, Pageable pageable);

}
