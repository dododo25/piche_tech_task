package com.piche.task.controller;

import com.piche.task.dto.AccountOperationResponseDTO;
import com.piche.task.model.Account;
import com.piche.task.repository.AccountRepository;
import com.piche.task.service.AccountDepositOperationService;
import com.piche.task.service.AccountService;
import com.piche.task.service.AccountTransferOperationService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountDepositOperationService accountDepositOperationService;

    @MockBean
    private AccountTransferOperationService accountTransferOperationService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @Test
    void testGetAllAccountsShouldReturnList() throws Exception {
        Account a1 = Account.builder()
                .id(1L)
                .name("Alice")
                .passwordHash("password_hash1")
                .build();

        Account a2 = Account.builder()
                .id(2L)
                .name("Bob")
                .passwordHash("password_hash2")
                .build();

        when(accountService.findAll()).thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(a1.getId()))
                .andExpect(jsonPath("$[0].name").value(a1.getName()))
                .andExpect(jsonPath("$[0].passwordHash").value(a1.getPasswordHash()))
                .andExpect(jsonPath("$[1].id").value(a2.getId()))
                .andExpect(jsonPath("$[1].name").value(a2.getName()))
                .andExpect(jsonPath("$[1].passwordHash").value(a2.getPasswordHash()));
    }

    @Test
    void testGetByIdShouldReturnObject() throws Exception {
        Account account = Account.builder()
                .id(1L)
                .name("Alice")
                .passwordHash("password_hash")
                .build();

        when(accountService.findById(1L)).thenReturn(account);

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(jsonPath("$.passwordHash").value(account.getPasswordHash()));
    }

    @Test
    void testGetAccountByNameShouldReturnObject() throws Exception {
        Account account = Account.builder()
                .id(1L)
                .name("Alice")
                .passwordHash("password_hash")
                .build();

        when(accountService.findByName("Alice")).thenReturn(account);

        mockMvc.perform(get("/account?name=Alice"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(jsonPath("$.passwordHash").value(account.getPasswordHash()));
    }

    @Test
    void testSaveAccountShouldReturnObject() throws Exception {
        JSONObject json = new JSONObject()
                .put("name", "Alice")
                .put("password", "paSSw0rD");

        Account expected = Account.builder()
                .id(1L)
                .name(json.getString("name"))
                .passwordHash("password_hash")
                .balance(0.0)
                .build();

        when(accountService.save(any())).thenReturn(expected);

        mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON_VALUE).content(json.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.passwordHash").value(expected.getPasswordHash()))
                .andExpect(jsonPath("$.balance").value(expected.getBalance()));
    }

    @Test
    void testValidateAccountShouldReturnObject() throws Exception {
        JSONObject json = new JSONObject()
                .put("name", "Alice")
                .put("password", "paSSw0rD");

        Account expected = Account.builder()
                .id(1L)
                .name(json.getString("name"))
                .passwordHash("c5f3e5e29ed62f801a751f2975a62d96114eff72f4ec031bb426a196caaca061")
                .balance(0.0)
                .build();

        when(accountService.findByName("Alice")).thenReturn(expected);

        mockMvc.perform(post("/account/validate").contentType(MediaType.APPLICATION_JSON_VALUE).content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAccountShouldDoneWell() throws Exception {
        mockMvc.perform(delete("/account/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllAccountOperationsShouldReturnList() throws Exception {
        AccountOperationResponseDTO r1 = AccountOperationResponseDTO.builder()
                .id(1101L)
                .type("deposit")
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountOperationResponseDTO r2 = AccountOperationResponseDTO.builder()
                .id(1201L)
                .type("transfer")
                .role("sender")
                .deposit(-25000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        AccountOperationResponseDTO r3 = AccountOperationResponseDTO.builder()
                .id(1202L)
                .type("transfer")
                .role("receiver")
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 3, 0, 0, 0))
                .build();

        when(accountService.getAllOperations(eq(1001L), any())).thenReturn(Arrays.asList(r1, r2, r3));

        mockMvc.perform(get("/account/1001/operation/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1101L))
                .andExpect(jsonPath("$[0].type").value("deposit"))
                .andExpect(jsonPath("$[1].id").value(1201L))
                .andExpect(jsonPath("$[1].type").value("transfer"))
                .andExpect(jsonPath("$[1].role").value("sender"))
                .andExpect(jsonPath("$[1].deposit").value(-25000.0))
                .andExpect(jsonPath("$[2].id").value(1202L))
                .andExpect(jsonPath("$[2].type").value("transfer"))
                .andExpect(jsonPath("$[2].role").value("receiver"))
                .andExpect(jsonPath("$[2].deposit").value(125000.0));
    }

    @Test
    void testGetAllAccountOperationsByDateSpanShouldReturnList() throws Exception {
        AccountOperationResponseDTO r1 = AccountOperationResponseDTO.builder()
                .id(1101L)
                .type("deposit")
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountOperationResponseDTO r2 = AccountOperationResponseDTO.builder()
                .id(1201L)
                .type("transfer")
                .role("sender")
                .deposit(-125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        AccountOperationResponseDTO r3 = AccountOperationResponseDTO.builder()
                .id(1202L)
                .type("transfer")
                .role("receiver")
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 3, 0, 0, 0))
                .build();

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        when(accountService.getAllOperationsByDateSpan(eq(1001L), any(), any(), any()))
                .thenReturn(Arrays.asList(r1, r2, r3));

        mockMvc.perform(get(String.format("/account/1001/operation/all?from=%s&to=%s", from, to)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1101L))
                .andExpect(jsonPath("$[0].type").value("deposit"))
                .andExpect(jsonPath("$[0].deposit").value(250000.0))
                .andExpect(jsonPath("$[1].id").value(1201L))
                .andExpect(jsonPath("$[1].type").value("transfer"))
                .andExpect(jsonPath("$[1].role").value("sender"))
                .andExpect(jsonPath("$[1].deposit").value(-125000.0))
                .andExpect(jsonPath("$[2].id").value(1202L))
                .andExpect(jsonPath("$[2].type").value("transfer"))
                .andExpect(jsonPath("$[2].role").value("receiver"))
                .andExpect(jsonPath("$[2].deposit").value(125000.0));
    }
}