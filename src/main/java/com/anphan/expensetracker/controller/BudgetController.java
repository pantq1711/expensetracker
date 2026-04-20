package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.BudgetDTO;
import com.anphan.expensetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "APIs for setting and managing spending budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "Get all budgets (ADMIN only)", description = "Retrieves a paginated list of all budgets. Requires ADMIN role.")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BudgetDTO>> getAllBudgets(Pageable pageable) {
        return ResponseEntity.ok(budgetService.getAllBudgets(pageable));
    }

    @Operation(summary = "Get budgets of the current user", description = "Retrieves a paginated list of budgets for the authenticated user, sorted by amount descending.")
    @GetMapping
    public ResponseEntity<Page<BudgetDTO>> getBudgetsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("amount").descending());
        return ResponseEntity.ok(budgetService.getBudgetsByUser(pageable));
    }

    @Operation(summary = "Get budget details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id){
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody BudgetDTO budgetDTO){
        return ResponseEntity.status(201).body(budgetService.createBudget(budgetDTO));
    }

    @Operation(summary = "Update budget by ID")
    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetDTO budgetDTO){
        return ResponseEntity.ok().body(budgetService.updateBudget(id, budgetDTO));
    }

    @Operation(summary = "Delete a budget by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id){
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}