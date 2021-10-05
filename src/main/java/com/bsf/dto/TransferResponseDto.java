package com.bsf.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TransferResponseDto {
    UUID transactionId;
    String message;
}
