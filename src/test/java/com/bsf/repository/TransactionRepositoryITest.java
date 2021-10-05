package com.bsf.repository;

import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TransactionRepositoryITest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void shouldStoreTransaction() {
        // given
        // source account
        final var source = new AccountEntity();
        source.setId(UUID.randomUUID());
        source.setBalance(BigDecimal.TEN);
        source.setName("SOURCE111");
        accountRepository.save(source);
        // target account
        final var target = new AccountEntity();
        target.setId(UUID.randomUUID());
        target.setBalance(BigDecimal.TEN);
        target.setName("TARGET111");
        accountRepository.save(target);

        final var transaction = new TransactionEntity();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSource(source);
        transaction.setTarget(target);
        // when
        final var result = transactionRepository.save(transaction);
        // then
        assertThat(result).usingComparatorForType(BigDecimal::compareTo, BigDecimal.class).usingRecursiveComparison().isEqualTo(transaction);
        final var storedEntity = transactionRepository.findById(transaction.getId());
        assertThat(storedEntity).isPresent();
        assertThat(storedEntity.get())
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(transaction);
    }

}
