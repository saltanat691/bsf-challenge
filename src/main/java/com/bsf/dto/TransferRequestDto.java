package com.bsf.dto;


import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class TransferRequestDto {
    @NotNull
    UUID source;
    @NotNull
    UUID target;
    @NotNull
    BigDecimal amount;
}
