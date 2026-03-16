package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByUser(User user);
}
