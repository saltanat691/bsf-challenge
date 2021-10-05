package com.bsf.service;

import com.bsf.dto.TransferRequestDto;
import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy
    @InjectMocks
    private TransferService transferService;

    @Test
    public void shouldTransfer() {
        // given
        final var sourceId = UUID.randomUUID();
        final var source = new AccountEntity();
        source.setId(sourceId);
        doReturn(Optional.of(source)).when(accountRepository).findByIdWithLock(sourceId);
        final var targetId = UUID.randomUUID();
        final var target = new AccountEntity();
        target.setId(targetId);
        doReturn(Optional.of(target)).when(accountRepository).findByIdWithLock(targetId);
        final var request = TransferRequestDto.builder()
                .source(sourceId)
                .target(targetId)
                .amount(BigDecimal.ONE)
                .build();
        doReturn(false).when(transferService).isInsufficientBalance(source, request);
        doNothing().when(transferService).moveMoney(source, target, BigDecimal.ONE);
        final var storedTransaction = new TransactionEntity();
        storedTransaction.setId(UUID.randomUUID());
        doReturn(storedTransaction).when(transferService).storeTransaction(source, target, BigDecimal.ONE);
        // when
        final var result = transferService.transfer(request);
        // then
        assertThat(result.getTransactionId()).isEqualTo(storedTransaction.getId());
        verify(accountRepository).findByIdWithLock(sourceId);
        verify(accountRepository).findByIdWithLock(targetId);
        verify(transferService).isInsufficientBalance(source, request);
        verify(transferService).moveMoney(source, target, BigDecimal.ONE);
        verify(transferService).storeTransaction(source, target, BigDecimal.ONE);
        verifyNoInteractions(transactionRepository);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void shouldNotTransfer_InsufficientFunds() {
        // given
        final var sourceId = UUID.randomUUID();
        final var source = new AccountEntity();
        source.setId(sourceId);
        doReturn(Optional.of(source)).when(accountRepository).findByIdWithLock(sourceId);
        final var targetId = UUID.randomUUID();
        final var target = new AccountEntity();
        target.setId(targetId);
        doReturn(Optional.of(target)).when(accountRepository).findByIdWithLock(targetId);
        final var request = TransferRequestDto.builder()
                .source(sourceId)
                .target(targetId)
                .amount(BigDecimal.ONE)
                .build();
        doReturn(true).when(transferService).isInsufficientBalance(source, request);
        // when
        final var result = transferService.transfer(request);
        // then
        assertThat(result.getMessage()).isEqualTo("Insufficient funds");
        verify(accountRepository).findByIdWithLock(sourceId);
        verify(accountRepository).findByIdWithLock(targetId);
        verify(transferService).isInsufficientBalance(source, request);
        verifyNoInteractions(transactionRepository);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void shouldNotTransfer_TargetNotFound() {
        // given
        final var sourceId = UUID.randomUUID();
        final var source = new AccountEntity();
        source.setId(sourceId);
        doReturn(Optional.of(source)).when(accountRepository).findByIdWithLock(sourceId);
        final var targetId = UUID.randomUUID();
        doReturn(Optional.empty()).when(accountRepository).findByIdWithLock(targetId);
        final var request = TransferRequestDto.builder()
                .source(sourceId)
                .target(targetId)
                .amount(BigDecimal.ONE)
                .build();
        // when
        final var e = assertThrows(ResponseStatusException.class, () -> transferService.transfer(request));
        // then
        assertThat(e.getReason()).isEqualTo("Target account is not found");
        assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(accountRepository).findByIdWithLock(sourceId);
        verify(accountRepository).findByIdWithLock(targetId);
        verifyNoInteractions(transactionRepository);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void shouldNotTransfer_SourceNotFound() {
        // given
        final var sourceId = UUID.randomUUID();
        doReturn(Optional.empty()).when(accountRepository).findByIdWithLock(sourceId);
        final var request = TransferRequestDto.builder()
                .source(sourceId)
                .amount(BigDecimal.ONE)
                .build();
        // when
        final var e = assertThrows(ResponseStatusException.class, () -> transferService.transfer(request));
        // then
        assertThat(e.getReason()).isEqualTo("Source account is not found");
        assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(accountRepository).findByIdWithLock(sourceId);
        verifyNoInteractions(transactionRepository);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void shouldStoreTransaction() {
        // given
        final var source = new AccountEntity();
        final var target = new AccountEntity();
        when(transactionRepository.save(any())).thenReturn(new TransactionEntity());
        // when
        final var response = transferService.storeTransaction(source, target, BigDecimal.ONE);
        // then
        assertThat(response).isNotNull();
        verify(transactionRepository).save(any());
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(accountRepository);
    }

    @Test
    public void shouldMoveMoney() {
        // given
        final var source = new AccountEntity();
        source.setBalance(BigDecimal.TEN);
        final var target = new AccountEntity();
        target.setBalance(BigDecimal.TEN);
        // when
        transferService.moveMoney(source, target, BigDecimal.ONE);
        // then
        verify(accountRepository).saveAll(Arrays.asList(source, target));
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    public void isInsufficientBalanceGreater() {
        // given
        final var source = new AccountEntity();
        source.setBalance(BigDecimal.TEN);
        final var request = TransferRequestDto.builder()
                .amount(BigDecimal.ONE)
                .build();
        // when
        final var response = transferService.isInsufficientBalance(source, request);
        // then
        assertThat(response).isEqualTo(false);
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    public void isInsufficientBalanceEqual() {
        // given
        final var source = new AccountEntity();
        source.setBalance(BigDecimal.TEN);
        final var request = TransferRequestDto.builder()
                .amount(BigDecimal.TEN)
                .build();
        // when
        final var response = transferService.isInsufficientBalance(source, request);
        // then
        assertThat(response).isEqualTo(false);
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    public void isInsufficientBalanceLess() {
        // given
        final var source = new AccountEntity();
        source.setBalance(BigDecimal.ONE);
        final var request = TransferRequestDto.builder()
                .amount(BigDecimal.TEN)
                .build();
        // when
        final var response = transferService.isInsufficientBalance(source, request);
        // then
        assertThat(response).isEqualTo(true);
        verifyNoInteractions(accountRepository, transactionRepository);
    }
}