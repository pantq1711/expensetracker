package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    //get all transaction
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransaction(){
        return ResponseEntity.ok().body(transactionService.getAllTransaction());
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
