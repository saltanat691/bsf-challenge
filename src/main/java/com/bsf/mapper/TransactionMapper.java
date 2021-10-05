package com.bsf.mapper;

import com.bsf.dto.TransactionDto;
import com.bsf.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto fromEntity(final TransactionEntity entity){
        return TransactionDto.builder()
                .id(entity.getId())
                .from(entity.getSource().getName())
                .to(entity.getTarget().getName())
                .amount(entity.getAmount())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
