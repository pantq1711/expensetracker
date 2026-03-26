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

    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    private Budget getBudgetAndCheckOwnership(Long id){
        Budget budget = budgetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách: " + id));

        User currentUser = securityUtils.getCurrentUser();

        if(!budget.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("Bạn không có quyền truy cập!");
        }
        return budget;
    }

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
        return convertToDTO(getBudgetAndCheckOwnership(id));
    }


    //update budget
    public BudgetDTO updateBudget(Long id, BudgetDTO dto){
        Budget budget = getBudgetAndCheckOwnership(id);
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
        budgetRepository.delete(getBudgetAndCheckOwnership(id));
    }

    private User getCurrentUser(){
        return securityUtils.getCurrentUser();
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
