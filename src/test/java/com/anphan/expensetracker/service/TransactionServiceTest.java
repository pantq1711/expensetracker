package com.anphan.expensetracker.service;

import com.anphan.expensetracker.constant.MessageConstants;
import com.anphan.expensetracker.dto.TransactionDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.Transaction;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import com.anphan.expensetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    void createTransaction_WhenValidInput_ShouldReturnDto() {
        // TODO
//         ARRANGE
        Category category = new Category();
        category.setId(99L);
        category.setUser(mockUser);
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("150000"));
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(category));
        //boolean trong test mặc định là false -> bật lên để test
        when(securityUtils.isAdminOrOwner(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        // ACT
        TransactionDTO result = transactionService.createTransaction(transactionDTO);

        // ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(99L, result.getCategoryId()),
                () -> assertTrue(new BigDecimal("150000").compareTo(result.getAmount()) == 0)
        );

        verify(transactionRepository, times(1)).save(any(Transaction.class));

    }

    @Test
    void createTransaction_WhenCategoryNotFound_ShouldThrowException() {
        // TODO
        // ARRANGE
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(transactionDTO));

        String expectedMessage = String.format(com.anphan.expensetracker.constant.MessageConstants.CATEGORY_NOT_FOUND, 99L);
        assertEquals(expectedMessage, exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));

    }

    @Test
    void updateTransaction_WhenNotOwner_ShouldThrowAccessDeniedException() {
        // TODO
        //Arrange
        User realOwner = new User();
        realOwner.setId(2L);
        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setUser(realOwner);
        when(transactionRepository.findById(100L)).thenReturn(Optional.of(transaction));

        //Act + Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> transactionService.updateTransaction(100L, transactionDTO));

        String expectedMessage = String.format(MessageConstants.UNAUTHORIZED_ACTION, 100L);
        assertEquals(expectedMessage, exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getAllTransactions_WhenPageable_ShouldReturnMappedPage() {
        // TODO
        //Arrange
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);

        Pageable pageable = PageRequest.of(0, 10);

        Transaction mockTs = new Transaction();
        mockTs.setId(100L);
        mockTs.setAmount(new BigDecimal("150000"));

        Category mockCategory = new Category();
        mockCategory.setId(99L);
        mockTs.setCategory(mockCategory);

        List<Transaction> mockList = List.of(mockTs);

        //boc list trong page
        Page<Transaction> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(transactionRepository.findByUser(mockUser, pageable)).thenReturn(mockPage);

        //Act
        Page<TransactionDTO> result = transactionService.getAllTransactions(pageable);

        //Assert
        assertNotNull(result);
        assertAll(
                () -> assertEquals(1L, result.getTotalElements()),
                () -> assertEquals(100L, result.getContent().get(0).getId()),
                () -> assertEquals(99L, result.getContent().get(0).getCategoryId()),
                () -> assertTrue(new BigDecimal("150000").compareTo(result.getContent().get(0).getAmount()) == 0)
        );
    }
}
