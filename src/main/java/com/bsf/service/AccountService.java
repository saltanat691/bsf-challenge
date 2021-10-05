package com.bsf.service;

import com.bsf.dto.AccountDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import com.bsf.mapper.AccountMapper;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountDto getDetails(final UUID id) {
        final AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        final List<TransactionEntity> transactions = transactionRepository
                .findAllBySourceOrTarget(account);
        return accountMapper.fromEntity(account, transactions);
    }
}
