package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import com.anphan.expensetracker.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private com.anphan.expensetracker.util.SecurityUtils securityUtils;

    @InjectMocks
    private TransactionService transactionService;

    private User mockUser;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp(){
        mockUser = new User();
        mockUser.setId(1L);

        transactionDTO = new TransactionDTO();
        transactionDTO.setCategoryId(99L);
        transactionDTO.setDate(LocalDate.now());
        transactionDTO.setAmount(new BigDecimal("150000"));
    }

    @Test
    void createTransaction_WhenCategoryNotFound_ShouldThrowException() {

        //ARRANGE
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        //ACT
        //kiem tra dung loai exception, sai -> test fail
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(transactionDTO));

        //ASSERT
        //kiem tra noi dung loi
        assertEquals("Category not found with ID: 99", exception.getMessage());

        //xac nhan save ko duoc goi vi ko gan duoc category id -> ko tao dc transaction
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WhenSuccess_ShouldReturnDto() {

    }

}
