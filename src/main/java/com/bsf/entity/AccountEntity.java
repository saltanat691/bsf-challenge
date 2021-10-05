package com.bsf.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class AccountEntity {
    @Id
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private BigDecimal balance;
}
