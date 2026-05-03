package com.anphan.expensetracker.controller;
import com.anphan.expensetracker.dto.DepositDTO;
import com.anphan.expensetracker.dto.UpdateBudgetDTO;
import com.anphan.expensetracker.dto.WalletCreateDTO;
import com.anphan.expensetracker.dto.WalletResponseDTO;
import com.anphan.expensetracker.repository.UserRepository;
import com.anphan.expensetracker.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
@Tag(name = "Shared Wallets", description = "APIs for managing shared wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @Operation(summary = "Get all wallets the current user belongs to")
    @GetMapping
    public ResponseEntity<List<WalletResponseDTO>> getMyWallets() {
        return ResponseEntity.ok(walletService.getMyWallets());
    }

    @Operation(summary = "Get wallet details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> getWalletById(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @Operation(summary = "Create a new shared wallet")
    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody WalletCreateDTO dto) {
        return ResponseEntity.status(201).body(walletService.createWallet(dto));
    }

    @Operation(summary = "Deposit into wallet (any member)")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletResponseDTO> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositDTO dto) {
        return ResponseEntity.ok(walletService.deposit(id, dto.getAmount()));
    }

    @Operation(summary = "Update wallet budget (OWNER only)")
    @PutMapping("/{id}/budget")
    public ResponseEntity<WalletResponseDTO> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetDTO dto) {
        return ResponseEntity.ok(walletService.updateBudget(id, dto.getBudget()));
    }

    @Operation(summary = "Add member to wallet (OWNER only)")
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long id,
            @PathVariable Long userId) {
        walletService.addMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}