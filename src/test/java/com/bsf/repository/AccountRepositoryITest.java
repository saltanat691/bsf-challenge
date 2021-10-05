package com.bsf.repository;

import com.bsf.entity.AccountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AccountRepositoryITest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void shouldStoreAccount() {
        // given
        final var account = new AccountEntity();
        account.setId(UUID.randomUUID());
        account.setBalance(BigDecimal.TEN);
        account.setName("test");
        // when
        final var result = accountRepository.save(account);
        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(account);
    }

    @Test
    public void shouldFetchAccountById() {
        // given
        final var account = new AccountEntity();
        account.setId(UUID.randomUUID());
        account.setBalance(BigDecimal.TEN);
        account.setName("John Doe");
        accountRepository.save(account);
        // when
        final var result = accountRepository.findById(account.getId());
        // then
        assertThat(result).isPresent();
        assertThat(result.get())
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(account);
    }

    @Test
    // transactional, because lock requires transaction
    @Transactional
    public void shouldFetchAccountByIdWithLock() {
        // given
        final var account = new AccountEntity();
        account.setId(UUID.randomUUID());
        account.setBalance(BigDecimal.TEN);
        account.setName("John Doe");
        accountRepository.save(account);
        // when
        final var result = accountRepository.findByIdWithLock(account.getId());
        // then
        assertThat(result).isPresent();
        assertThat(result.get())
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(account);
    }
}
