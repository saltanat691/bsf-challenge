package com.bsf.mapper;

import com.bsf.dto.TransactionDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AccountMapperTest {

    @InjectMocks
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Test
    public void mapAccount(){
        // given
        final var account = new AccountEntity();
        account.setBalance(BigDecimal.TEN);
        account.setId(UUID.randomUUID());
        account.setName("Jane Doe");
        final var transaction = new TransactionEntity();
        final var transactions = Collections.singletonList(transaction);
        when(transactionMapper.fromEntity(transaction)).thenReturn(TransactionDto.builder().build());
        // when
        final var result = accountMapper.fromEntity(account, transactions);
        // then
        assertThat(result.getAccountNumber()).isEqualTo(account.getId());
        assertThat(result.getBalance()).isEqualTo(account.getBalance());
        assertThat(result.getTransactions()).hasSize(1);
        verify(transactionMapper).fromEntity(transaction);
    }
}
