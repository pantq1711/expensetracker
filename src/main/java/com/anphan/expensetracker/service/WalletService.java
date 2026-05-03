package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.WalletCreateDTO;
import com.anphan.expensetracker.dto.WalletResponseDTO;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.entity.Wallet;
import com.anphan.expensetracker.entity.WalletMember;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.UserRepository;
import com.anphan.expensetracker.repository.WalletMemberRepository;
import com.anphan.expensetracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletMemberRepository walletMemberRepository;
    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    // Lấy wallet + kiểm tra user có phải member không
    private Wallet getWalletAndCheckMembership(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));

        User current = securityUtils.getCurrentUser();
        walletMemberRepository.findByWalletAndUser(wallet, current)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this wallet"));

        return wallet;
    }

    // Lấy wallet + kiểm tra user có phải OWNER không
    private Wallet getWalletAndCheckOwner(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));

        User current = securityUtils.getCurrentUser();
        WalletMember member = walletMemberRepository.findByWalletAndUser(wallet, current)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this wallet"));

        if (member.getRole() != WalletMember.Role.OWNER) {
            throw new AccessDeniedException("Only OWNER can perform this action");
        }

        return wallet;
    }

    // Tao moi
    @Transactional
    public WalletResponseDTO createWallet(WalletCreateDTO dto) {
        User current = securityUtils.getCurrentUser();

        Wallet wallet = Wallet.builder()
                .name(dto.getName())
                .balance(BigDecimal.ZERO)
                .budget(dto.getBudget())
                .build();

        walletRepository.save(wallet);

        // Người tạo tự động trở thành OWNER
        WalletMember owner = WalletMember.builder()
                .wallet(wallet)
                .user(current)
                .contribution(BigDecimal.ZERO)
                .role(WalletMember.Role.OWNER)
                .build();

        walletMemberRepository.save(owner);

        return convertToDTO(wallet);
    }

    public List<WalletResponseDTO> getMyWallets() {
        User current = securityUtils.getCurrentUser();
        return walletRepository.findByMember(current)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public WalletResponseDTO getWalletById(Long walletId) {
        return convertToDTO(getWalletAndCheckMembership(walletId));
    }

    // Deposit — dùng atomic update cho balance
    @Transactional
    public WalletResponseDTO deposit(Long walletId, BigDecimal amount) {
        User current = securityUtils.getCurrentUser();

        // Kiểm tra membership trước
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));

        WalletMember member = walletMemberRepository.findByWalletAndUser(wallet, current)
                .orElseThrow(() -> new AccessDeniedException("Not a member"));

        // Atomic update — DB tự xử lý concurrency
        int updated = walletRepository.addBalance(walletId, amount);
        if (updated == 0) throw new ResourceNotFoundException("Wallet not found");

        // Contribution cũng atomic
        walletMemberRepository.addContribution(member.getId(), amount);

        return convertToDTO(walletRepository.findById(walletId).orElseThrow());
    }

    // Update name/budget — dùng optimistic locking, ít conflict hơn nên retry là đủ
    @Transactional
    public WalletResponseDTO updateBudget(Long walletId, BigDecimal newBudget) {
        int maxRetries = 3;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                Wallet wallet = getWalletAndCheckOwner(walletId);
                wallet.setBudget(newBudget);
                walletRepository.save(wallet);
                return convertToDTO(wallet);
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == maxRetries - 1) {
                    throw new RuntimeException("Wallet is being updated. Please try again.");
                }
                // Re-fetch sẽ xảy ra ở vòng lặp tiếp theo
            }
        }
        throw new RuntimeException("Unexpected error");
    }

    @Transactional
    public void addMember(Long walletId, Long userId) {
        Wallet wallet = getWalletAndCheckOwner(walletId);

        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Kiểm tra đã là member chưa
        if (walletMemberRepository.findByWalletAndUser(wallet, newUser).isPresent()) {
            throw new RuntimeException("User is already a member of this wallet");
        }

        WalletMember member = WalletMember.builder()
                .wallet(wallet)
                .user(newUser)
                .contribution(BigDecimal.ZERO)
                .role(WalletMember.Role.MEMBER)
                .build();

        walletMemberRepository.save(member);
    }

    private WalletResponseDTO convertToDTO(Wallet wallet) {
        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setName(wallet.getName());
        dto.setBalance(wallet.getBalance());
        dto.setBudget(wallet.getBudget());
        // availableBalance tính ở đây, không lưu DB
        BigDecimal budget = wallet.getBudget() != null ? wallet.getBudget() : BigDecimal.ZERO;
        dto.setAvailableBalance(wallet.getBalance().subtract(budget));
        return dto;
    }
}