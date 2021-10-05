package com.bsf.controller;


import com.bsf.dto.TransferRequestDto;
import com.bsf.dto.TransferResponseDto;
import com.bsf.entity.AccountEntity;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldReturn200WhenRequestIsCorrect() throws Exception {
        //given
        //source account
        final var source = new AccountEntity();
        source.setId(UUID.randomUUID());
        source.setBalance(BigDecimal.TEN);
        source.setName("SOURCE111");
        accountRepository.save(source);
        //target account
        final var target = new AccountEntity();
        target.setId(UUID.randomUUID());
        target.setBalance(BigDecimal.TEN);
        target.setName("TARGET111");
        accountRepository.save(target);
        final var request = TransferRequestDto.builder()
                .source(source.getId())
                .target(target.getId())
                .amount(BigDecimal.ONE)
                .build();
        final var requestJson = mapper.writeValueAsString(request);
        // when
        final var responseJson = mockMvc.perform(post("/transfer/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // then
        final var response = mapper.readValue(responseJson, TransferResponseDto.class);
        assertThat(response.getTransactionId()).isNotNull();
        final var sourceFromDb = accountRepository.findById(source.getId()).orElseThrow();
        assertThat(sourceFromDb.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(9));
        final var targetFromDb = accountRepository.findById(target.getId()).orElseThrow();
        assertThat(targetFromDb.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(11));
    }

    @Test
    public void shouldReturn404WhenAccountIsIncorrect() throws Exception {
        // given
        final var request = TransferRequestDto.builder()
                .source(UUID.randomUUID())
                .target(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .build();
        final var requestJson = mapper.writeValueAsString(request);
        // when
        // then
        mockMvc.perform(post("/transfer/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn400WhenRequestIsIncorrect_MissingSource() throws Exception {
        // given
        final var request = TransferRequestDto.builder()
                .target(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .build();
        final var requestJson = mapper.writeValueAsString(request);
        // when
        // then
        mockMvc.perform(post("/transfer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenRequestIsIncorrect_MissingTarget() throws Exception {
        // given
        final var request = TransferRequestDto.builder()
                .source(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .build();
        final var requestJson = mapper.writeValueAsString(request);
        // when
        // then
        mockMvc.perform(post("/transfer/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenRequestIsIncorrect_MissingAmount() throws Exception {
        // given
        final var request = TransferRequestDto.builder()
                .source(UUID.randomUUID())
                .target(UUID.randomUUID())
                .build();
        final var requestJson = mapper.writeValueAsString(request);
        // when
        // then
        mockMvc.perform(post("/transfer/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
