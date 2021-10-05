package com.bsf.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class TransactionDto {
    UUID id;
    String from;
    String to;
    BigDecimal amount;
    Instant timestamp;
}
