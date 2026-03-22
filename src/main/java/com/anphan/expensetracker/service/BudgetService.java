package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.BudgetDTO;
import com.anphan.expensetracker.entity.Budget;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;

    //get All Budget
    public Page<BudgetDTO> getAllBudgets(Pageable pageable){ // TODO: only for admin
        return budgetRepository.findAll(pageable)
                .map(this :: convertToDTO);

    }
    //get All Budget + pagination
    public Page<BudgetDTO> getBudgetsByUser(Pageable pageable){
        return budgetRepository.findByUser(getCurrentUser(), pageable)
                .map(this :: convertToDTO);
    }

    //get budget by id
    public BudgetDTO getBudgetById(Long id){
        Budget budget = getBudgetByIdAndCheckOwner(id);
        return convertToDTO(budget);
    }


    //update budget
    public BudgetDTO updateBudget(Long id, BudgetDTO dto){
        Budget budget = getBudgetByIdAndCheckOwner(id);
        budget.setAmount(dto.getAmount());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        if(dto.getCategoryId() != null){
            Category category = new Category();
            category.setId(dto.getCategoryId());
            budget.setCategory(category);
        }
        budgetRepository.save(budget);
        return convertToDTO(budget);
    }

    //create budget
    public BudgetDTO createBudget(BudgetDTO dto){
        Budget budget = new Budget();
        budget.setAmount(dto.getAmount());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        if(dto.getCategoryId() != null){
            Category category = new Category();
            category.setId(dto.getCategoryId());
            budget.setCategory(category);
        }
        budget.setUser(getCurrentUser());
        budgetRepository.save(budget);
        return convertToDTO(budget);
    }

    //delete budget
    public void deleteBudget(Long id){
        Budget budget = getBudgetByIdAndCheckOwner(id);
        budgetRepository.delete(budget);
    }

    private Budget getBudgetByIdAndCheckOwner(Long id){
        Budget budget = budgetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found budget"));
        if(!budget.getUser().getId().equals(getCurrentUser().getId())){
            throw new RuntimeException("Forbidden");
        }
        return budget;
    }

    private User getCurrentUser(){
        User user = new User();
        user.setId(2L);
        return user;
    }

    private BudgetDTO convertToDTO(Budget budget){
        BudgetDTO dto = new BudgetDTO();
        dto.setAmount(budget.getAmount());
        dto.setId(budget.getId());
        if(budget.getCategory() != null){
            dto.setCategoryId(budget.getCategory().getId());
            dto.setCategoryName(budget.getCategory().getName());
        }
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());
        return dto;
    }

}
