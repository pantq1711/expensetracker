package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    List<Category> findByUser(User user);
}
