package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.BudgetDTO;
import com.anphan.expensetracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    // get All Budget
    @GetMapping
    public ResponseEntity<Page<BudgetDTO>> getBudgetsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("amount").descending());
        return ResponseEntity.ok(budgetService.getBudgetsByUser(pageable));
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<BudgetDTO>> getAllBudgets(Pageable pageable){
        return ResponseEntity.ok(budgetService.getAllBudgets(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id){
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO budgetDTO){
        return ResponseEntity.status(201).body(budgetService.createBudget(budgetDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @RequestBody BudgetDTO budgetDTO){
        return ResponseEntity.ok().body(budgetService.updateBudget(id, budgetDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id){
         budgetService.deleteBudget(id);
         return ResponseEntity.noContent().build();
    }
}
