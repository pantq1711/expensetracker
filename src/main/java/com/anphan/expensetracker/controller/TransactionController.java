package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "APIs for managing income and expense cash flow")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get all transactions (Optional pagination)", description = "Returns all transactions for the user. Provides paginated results if page and size parameters are included.")
    @GetMapping
    public ResponseEntity<?> getAllTransaction(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size){
        if(page != null && size != null){
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
        }
        return ResponseEntity.ok(transactionService.getAllTransactionsForAdmin());
    }

    @Operation(summary = "Filter transactions by date range (Paginated)")
    @GetMapping("/filter")
    public ResponseEntity<Page<TransactionDTO>> getTransactionByUserAndDateBetween( @RequestParam LocalDate start, @RequestParam LocalDate end, Pageable pageable){
        return ResponseEntity.ok(transactionService.getTransactionByUserAndDateBetween(start, end, pageable));
    }

    @Operation(summary = "Get transaction details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id){
        return ResponseEntity.ok().body(transactionService.getTransactionById(id));
    }

    @Operation(summary = "Update transaction by ID")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO dto){
        return ResponseEntity.status(200).body(transactionService.updateTransaction(id, dto));
    }

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO dto){
        return ResponseEntity.status(201).body(transactionService.createTransaction(dto));
    }

    @Operation(summary = "Delete transaction by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}