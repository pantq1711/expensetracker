package com.anphan.expensetracker.service;
import com.anphan.expensetracker.entity.Note;
import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.FilterReportProjection;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TransactionService{

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    private Transaction getTransactionAndCheckOwnership(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch: " + id));

        User currentUser = getCurrentUser();

        if(!transaction.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("Bạn không có quyền truy cập");
        }
        return transaction;
    }

    public List<TransactionDTO> getAllTransaction(){
        return transactionRepository.findAll()
                .stream()
                .map(this :: convertToDTO)
                .toList();
    }

    public Page<TransactionDTO> getAllTransactions(Pageable pageable){
        return transactionRepository.findByUser(getCurrentUser(), pageable)
                .map(this :: convertToDTO);
    }

    public SummaryProjection getSummary(){
        return transactionRepository.sumByUser(getCurrentUser());
    }

    public List<CategoryReportDTO> getSummaryByCategory(){
        return transactionRepository.sumByCategoryName(getCurrentUser());
    }

    public Page<TransactionDTO> getTransactionByUserAndDateBetween(LocalDate start, LocalDate end, Pageable pageable){
        return transactionRepository.findByUserAndDateBetween(getCurrentUser(), start, end, pageable).map(this ::convertToDTO);
    }

    public TransactionDTO getTransactionById(Long id){
        return convertToDTO(getTransactionAndCheckOwnership(id));
    }
    public TransactionDTO updateTransaction(Long id, TransactionDTO dto){
        Transaction transaction = getTransactionAndCheckOwnership(id);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found category with ID: " + dto.getCategoryId()));
        transaction.setCategory(category);
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    public TransactionDTO createTransaction(TransactionDTO dto){
        Transaction transaction = new Transaction();
        transaction.setUser(getCurrentUser());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found category with ID: " + dto.getCategoryId()));
        transaction.setCategory(category);
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    public void deleteTransaction(Long id){
        transactionRepository.delete(getTransactionAndCheckOwnership(id));
    }

     private User getCurrentUser() {
     return securityUtils.getCurrentUser();
 }
    private TransactionDTO convertToDTO(Transaction transaction){
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setNote(transaction.getNote());
        dto.setDate(transaction.getDate());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found category with ID: " + dto.getCategoryId()));
        transaction.setCategory(category);
        return dto;
    }
}