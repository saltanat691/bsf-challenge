package com.bsf.controller;

import com.bsf.dto.AccountDto;
import com.bsf.service.AccountService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = AccountDto.class),
            @ApiResponse(code = 400, message = "Provided request is invalid"),
            @ApiResponse(code = 404, message = "Account not found"),
    })
    @GetMapping("/{id}")
    public AccountDto getAccountDetails(@PathVariable final UUID id){
        return accountService.getDetails(id);
    }
}
