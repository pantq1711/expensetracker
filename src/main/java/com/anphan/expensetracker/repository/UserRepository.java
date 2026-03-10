package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Spring tu gen SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    //Spring tu gen SQL: SELECT COUNT(*) FROM users WHERE email = ?
    boolean existsByEmail(String email);
}