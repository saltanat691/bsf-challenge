package com.bsf.service;

import com.bsf.dto.AccountDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import com.bsf.mapper.AccountMapper;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void shouldGiveAccountDetailsNew() {
        // given
        final var id = UUID.randomUUID();
        final var entity = new AccountEntity();
        final var transactions = new ArrayList<TransactionEntity>();
        final var expectedResponse = AccountDto.builder().build();
        doReturn(Optional.of(entity)).when(accountRepository).findById(id);
        doReturn(transactions).when(transactionRepository).findAllBySourceOrTarget(entity);
        doReturn(expectedResponse).when(accountMapper).fromEntity(entity, transactions);
        // when
        final var accountDto = accountService.getDetails(id);
        // then
        assertThat(accountDto).isEqualTo(expectedResponse);
        verify(accountRepository).findById(id);
        verify(transactionRepository).findAllBySourceOrTarget(entity);
        verify(accountMapper).fromEntity(entity, transactions);
        verifyNoMoreInteractions(accountRepository, transactionRepository, accountMapper);
    }

    @Test
    public void shouldThrowIfAccountIsNotFound() {
        // given
        final var id = UUID.randomUUID();
        doReturn(Optional.empty()).when(accountRepository).findById(id);
        // when
        final var exception = assertThrows(ResponseStatusException.class, () -> accountService.getDetails(id));
        // then
        assertThat(exception).isInstanceOf(ResponseStatusException.class);
        assertThat(exception.getReason()).isEqualTo("Account not found");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(accountRepository).findById(id);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository, accountMapper);
    }
}
