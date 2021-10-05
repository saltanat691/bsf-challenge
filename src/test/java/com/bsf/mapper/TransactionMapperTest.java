package com.bsf.mapper;

import com.bsf.entity.AccountEntity;
import com.bsf.entity.TransactionEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    @Test
    public void mapTransaction(){
        // given
        final var entity = new TransactionEntity();
        entity.setId(UUID.randomUUID());
        final var source = new AccountEntity();
        source.setName("source");
        entity.setSource(new AccountEntity());
        final var target = new AccountEntity();
        target.setName("target");
        entity.setTarget(new AccountEntity());
        entity.setAmount(BigDecimal.TEN);
        entity.setTimestamp(Instant.now());
        // when
        final var response = transactionMapper.fromEntity(entity);
        // then
        assertThat(response.getAmount()).isEqualTo(entity.getAmount());
        assertThat(response.getFrom()).isEqualTo(entity.getSource().getName());
        assertThat(response.getTo()).isEqualTo(entity.getTarget().getName());
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getTimestamp()).isEqualTo(entity.getTimestamp());
    }
}
