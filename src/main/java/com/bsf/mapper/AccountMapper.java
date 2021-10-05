package com.bsf.mapper;

import com.bsf.dto.AccountDto;
import com.bsf.dto.TransactionDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final TransactionMapper transactionMapper;

    public AccountDto fromEntity(
            final AccountEntity entity,
            final List<TransactionEntity> transactions
    ){
        final List<TransactionDto> mappedTransactions = transactions.stream()
                .map(transactionMapper::fromEntity)
                .collect(Collectors.toList());
        return AccountDto.builder()
                .accountNumber(entity.getId())
                .balance(entity.getBalance())
                .transactions(mappedTransactions)
                .build();
    }

}
