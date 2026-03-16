package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.loader.LoaderLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    // get All transaction
    public List<TransactionDTO> getAllTransaction(){
        return transactionRepository.findAll()
                .stream()
                .map(this :: convertToDTO)
                .toList();
    }

    // get 1 transaction
    public TransactionDTO getTransactionById(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Transaction" + id));
        return convertToDTO(transaction);
    }

    // update transaction
    public TransactionDTO updateTransaction(Long id, TransactionDTO dto){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Transaction" + id));
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setType(dto.getType());
        transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    //create transaction
    public TransactionDTO createTransaction(TransactionDTO dto){
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setType(dto.getType());
        transaction.setNote(dto.getNote());
        if(dto.getCategoryId() != null){
            Category category = new Category();
            category.setId(dto.getCategoryId());
            transaction.setCategory(category); // ← Thêm
        }

        User user = new User();
        user.setId(2L);
        transaction.setUser(user);
        transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    //delete transaction
    public void deleteTransaction(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Transaction" + id));
        transactionRepository.deleteById(id);
    }

    private TransactionDTO convertToDTO(Transaction transaction){
        TransactionDTO dto = new TransactionDTO();
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setType(transaction.getType());
        dto.setNote(transaction.getNote());
        dto.setId(transaction.getId());
        if(transaction.getCategory() != null) dto.setCategoryId(transaction.getCategory().getId());
        return dto;
    }
}
