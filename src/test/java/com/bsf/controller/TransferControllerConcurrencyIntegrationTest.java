package com.bsf.controller;

import com.bsf.dto.TransferRequestDto;
import com.bsf.dto.TransferResponseDto;
import com.bsf.entity.AccountEntity;
import com.bsf.repository.AccountRepository;
import com.bsf.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerConcurrencyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldHandleConcurrentRequests() throws Exception {
        // given
        final var numberOfRequests = 10;
        //source account
        final var source = new AccountEntity();
        source.setId(UUID.randomUUID());
        source.setBalance(BigDecimal.TEN);
        source.setName("source");
        accountRepository.save(source);
        //target account
        final var target = new AccountEntity();
        target.setId(UUID.randomUUID());
        target.setBalance(BigDecimal.TEN);
        target.setName("target");
        accountRepository.save(target);
        final var requests = generateRequests(numberOfRequests, source.getId(), target.getId());
        // when
        final var responses = requests.parallelStream()
                .map(this::sendRequest)
                .collect(Collectors.toList());
        // then
        assertThat(responses).hasSize(numberOfRequests);
        final var sourceFromDb = accountRepository.findById(source.getId()).orElseThrow();
        assertThat(sourceFromDb.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        final var targetFromDb = accountRepository.findById(target.getId()).orElseThrow();
        assertThat(targetFromDb.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(20));
        final var transactions = transactionRepository.findAllBySourceOrTarget(source);
        assertThat(transactions).hasSize(1);
    }

    @SneakyThrows
    private TransferResponseDto sendRequest(final String requestJson) {
        final var response = mockMvc.perform(post("/transfer/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readValue(response, TransferResponseDto.class);
    }

    private List<String> generateRequests(int n, final UUID sourceId, final UUID targetId)
            throws JsonProcessingException {
        final List<String> requests = new ArrayList<>();
        for (int i=0; i<n; i++) {
            requests.add(generateRequest(sourceId, targetId));
        }
        return requests;
    }

    private String generateRequest(final UUID sourceId, final UUID targetId) throws JsonProcessingException {
        final var request = TransferRequestDto.builder()
                .source(sourceId)
                .target(targetId)
                .amount(BigDecimal.TEN)
                .build();
        return mapper.writeValueAsString(request);
    }
}
