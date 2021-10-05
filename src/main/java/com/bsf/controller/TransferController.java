package com.bsf.controller;

import com.bsf.dto.TransferRequestDto;
import com.bsf.dto.TransferResponseDto;
import com.bsf.service.TransferService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Ok", response = TransferResponseDto.class),
            @ApiResponse(code = 400, message = "Required field is missing."),
    })
    @PostMapping
    public TransferResponseDto transfer(@RequestBody @Valid final TransferRequestDto request){
        return transferService.transfer(request);
    }
}
