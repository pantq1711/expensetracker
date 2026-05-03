package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.entity.Wallet;
import com.anphan.expensetracker.entity.WalletMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletMemberRepository extends JpaRepository<WalletMember, Long> {

    List<WalletMember> findByWallet(Wallet wallet);

    Optional<WalletMember> findByWalletAndUser(Wallet wallet, User user);

    @Modifying
    @Query("UPDATE WalletMember wm SET wm.contribution = wm.contribution + :amount WHERE wm.id = :id")
    int addContribution(@Param("id") Long id, @Param("amount")BigDecimal bigDecimal);
}
