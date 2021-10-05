package com.bsf.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class TransactionEntity {
    @Id
    private UUID id;
    @NotNull
    private BigDecimal amount;
    @NotNull
    @ManyToOne
    private AccountEntity source;
    @NotNull
    @ManyToOne
    private AccountEntity target;
    @NotNull
    private Instant timestamp = Instant.now();
}
