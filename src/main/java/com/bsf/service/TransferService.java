package com.bsf.service;

import com.bsf.dto.TransferRequestDto;
import com.bsf.dto.TransferResponseDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferResponseDto transfer(final TransferRequestDto request){
        final AccountEntity source = accountRepository.findByIdWithLock(request.getSource())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account is not found"));
        final AccountEntity target = accountRepository.findByIdWithLock(request.getTarget())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target account is not found"));
        if (isInsufficientBalance(source, request)) {
            return TransferResponseDto.builder().message("Insufficient funds").build();
        }
        moveMoney(source, target, request.getAmount());
        final TransactionEntity storedTransaction = storeTransaction(source, target, request.getAmount());
        return TransferResponseDto.builder().transactionId(storedTransaction.getId()).build();
    }

    boolean isInsufficientBalance(final AccountEntity source, final TransferRequestDto request) {
        return source.getBalance().compareTo(request.getAmount()) < 0;
    }

    void moveMoney(final AccountEntity source, final AccountEntity target, final BigDecimal amount) {
        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));
        accountRepository.saveAll(Arrays.asList(source, target));
    }

    TransactionEntity storeTransaction(final AccountEntity source,
                                       final AccountEntity target,
                                       final BigDecimal amount) {
        final TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setId(UUID.randomUUID());
        transactionEntity.setSource(source);
        transactionEntity.setTarget(target);
        transactionEntity.setAmount(amount);
        return transactionRepository.save(transactionEntity);
    }
}
