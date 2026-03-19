package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.loader.LoaderLogging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;
    // pagination
    public Page<TransactionDTO> getAllTransaction(Pageable pageable){
        User user = new User();
        user.setId(2L); //tam thoi hardcore
        Page<Transaction> page = transactionRepository.findByUser(user, pageable);
        return page.map(this :: convertToDTO);
    }
    //pagination with filter
    public Page<TransactionDTO> getTransactionByUserAndDateBetween(LocalDate start, LocalDate end, Pageable pageable){
        User user = new User();
        user.setId(2L);
        Page<Transaction> page = transactionRepository.findByUserAndDateBetween(user, start, end, pageable);
        return page.map(this :: convertToDTO);
    }

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
