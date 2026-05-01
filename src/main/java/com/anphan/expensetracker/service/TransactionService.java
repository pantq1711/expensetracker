package com.anphan.expensetracker.service;
import com.anphan.expensetracker.constant.MessageConstants;
import com.anphan.expensetracker.dto.CategoryReportDTO;
import com.anphan.expensetracker.dto.SummaryProjection;
import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TransactionService{
    private final ReportCacheService reportCacheService;

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    private Transaction getTransactionAndCheckOwnership(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Transaction: " + id));

        if(!securityUtils.isAdminOrOwner(transaction.getUser().getId())){
            throw new AccessDeniedException("You don't have permission to access!");
        }
        return transaction;
    }

    public List<TransactionDTO> getAllTransactionsForAdmin(){
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(com.anphan.expensetracker.constant.MessageConstants.CATEGORY_NOT_FOUND, dto.getCategoryId())
                ));
        if(!securityUtils.isAdminOrOwner(category.getUser().getId())){
            throw new AccessDeniedException(String.format(MessageConstants.UNAUTHORIZED_ACTION));
        }
        transaction.setCategory(category);
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transactionRepository.save(transaction);
        reportCacheService.invalidateUserReports(getCurrentUser().getId());
        return convertToDTO(transaction);
    }

    public TransactionDTO createTransaction(TransactionDTO dto){
        Transaction transaction = new Transaction();
        transaction.setUser(getCurrentUser());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(com.anphan.expensetracker.constant.MessageConstants.CATEGORY_NOT_FOUND, dto.getCategoryId())
                ));
        if(!securityUtils.isAdminOrOwner(category.getUser().getId())){
            throw new AccessDeniedException(String.format(MessageConstants.UNAUTHORIZED_ACTION));
        }
        transaction.setCategory(category);
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        Transaction savedTransaction = transactionRepository.save(transaction);
        reportCacheService.invalidateUserReports(getCurrentUser().getId());
        return convertToDTO(savedTransaction);
    }

    public void deleteTransaction(Long id){

        transactionRepository.delete(getTransactionAndCheckOwnership(id));
        reportCacheService.invalidateUserReports(getCurrentUser().getId());
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
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
        }
        return dto;
    }
}