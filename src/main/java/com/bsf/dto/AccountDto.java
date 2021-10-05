package com.bsf.dto;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class AccountDto {
    UUID accountNumber;
    BigDecimal balance;
    List<TransactionDto> transactions;

}
