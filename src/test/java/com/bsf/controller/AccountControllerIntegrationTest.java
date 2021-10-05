package com.bsf.controller;


import com.bsf.dto.AccountDto;
import com.bsf.entity.AccountEntity;
import com.bsf.mapper.AccountMapper;
import com.bsf.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Test
    public void shouldReturn200WhenRequestIsCorrect() throws Exception {
        // given
        final var id = UUID.randomUUID();
        final var entity = new AccountEntity();
        entity.setId(id);
        entity.setBalance(BigDecimal.ZERO);
        entity.setName("John Doe");
        accountRepository.save(entity);
        // when
        final var resultActions = mockMvc.perform(get("/account/"+ id)).andExpect(status().isOk());
        // then
        final var result = resultActions.andReturn();
        final var contentAsString = result.getResponse().getContentAsString();
        final var response = objectMapper.readValue(contentAsString, AccountDto.class);
        assertThat(response)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(accountMapper.fromEntity(entity, new ArrayList<>()));
    }

    @Test
    public void shouldReturn400WhenRequestIsIncorrect() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/account/"+123)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenAccountIsNotFound() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/account/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }
}
