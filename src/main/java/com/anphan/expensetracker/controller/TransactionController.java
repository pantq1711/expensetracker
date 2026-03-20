package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.service.TransactionService;
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
public class TransactionController {

    private final TransactionService transactionService;

    //get all transaction + pagination
    @GetMapping
    public ResponseEntity<?> getAllTransaction(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size){
                if(page != null && size != null){
                    Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
                    return ResponseEntity.ok(transactionService.getTransactionsByUser(pageable));
                }
                return ResponseEntity.ok(transactionService.getAllTransaction());
    }


    //pagination with date
    @GetMapping("/filter")
    public ResponseEntity<Page<TransactionDTO>> getTransactionByUserAndDateBetween( @RequestParam  LocalDate start, @RequestParam LocalDate end, Pageable pageable){
        return ResponseEntity.ok(transactionService.getTransactionByUserAndDateBetween(start, end, pageable));
    }

    // get transaction by id
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id){
        return ResponseEntity.ok().body(transactionService.getTransactionById(id));
    }

    //update transaction
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto){
        return ResponseEntity.status(200).body(transactionService.updateTransaction(id, dto));
    }

    //create transaction
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto){
        return ResponseEntity.status(201).body(transactionService.createTransaction(dto));
    }

    //delete transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
