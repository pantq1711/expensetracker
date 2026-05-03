package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT wm.wallet from WalletMember wm WHERE wm.user = :user")
    List<Wallet> findByMember(@Param("user") User user);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.id = :id")
    int addBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
