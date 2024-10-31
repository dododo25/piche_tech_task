package com.piche.task.controller;

import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.model.AccountTransferOperation;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
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
    void testGetByIdWhenAccountDoesNotExistsShouldReturnObject() throws Exception {
        mockMvc.perform(get("/account/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
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
    void testGetAccountByNameWhenAccountDoesNotExistsShouldReturnObject() throws Exception {
        mockMvc.perform(get("/account?name=Alice"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
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
    void testSaveAccountWhenAccountAlreadyExistsShouldReturnObject() throws Exception {
        JSONObject json = new JSONObject()
                .put("name", "Alice")
                .put("password", "paSSw0rD");

        Account account = Account.builder()
                .id(1L)
                .name(json.getString("name"))
                .passwordHash("password_hash")
                .balance(0.0)
                .build();

        when(accountService.findByName(account.getName())).thenReturn(account);

        mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON_VALUE).content(json.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Account with name '%s' already exists", json.getString("name"))));
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
    void testValidateAccountWhenPasswordIsWrongShouldReturnObject() throws Exception {
        JSONObject json = new JSONObject()
                .put("name", "Alice")
                .put("password", "password");

        Account expected = Account.builder()
                .id(1L)
                .name(json.getString("name"))
                .passwordHash("c5f3e5e29ed62f801a751f2975a62d96114eff72f4ec031bb426a196caaca061")
                .balance(0.0)
                .build();

        when(accountService.findByName("Alice")).thenReturn(expected);

        mockMvc.perform(post("/account/validate").contentType(MediaType.APPLICATION_JSON_VALUE).content(json.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Wrong password"));
    }

    @Test
    void testDeleteAccountShouldDoneWell() throws Exception {
        mockMvc.perform(delete("/account/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllAccountOperationsShouldReturnList() throws Exception {
        Account a1 = Account.builder()
                .id(1001L)
                .name("Alice")
                .passwordHash("password_hash1")
                .build();

        Account a2 = Account.builder()
                .id(1002L)
                .name("Bob")
                .passwordHash("password_hash2")
                .build();

        AccountDepositOperation depositOperation = AccountDepositOperation.builder()
                .id(1101L)
                .account(a1)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation1 = AccountTransferOperation.builder()
                .id(1201L)
                .sender(a1)
                .receiver(a2)
                .deposit(25000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation2 = AccountTransferOperation.builder()
                .id(1202L)
                .sender(a2)
                .receiver(a1)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 3, 0, 0, 0))
                .build();

        when(accountService.findById(1001L)).thenReturn(a1);
        when(accountDepositOperationService.findAllByAccountId(1001L))
                .thenReturn(Collections.singletonList(depositOperation));
        when(accountTransferOperationService.findAllBySenderId(1001L))
                .thenReturn(Collections.singletonList(transferOperation1));
        when(accountTransferOperationService.findAllByReceiverId(1001L))
                .thenReturn(Collections.singletonList(transferOperation2));

        mockMvc.perform(get("/account/1001/operation/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1202L))
                .andExpect(jsonPath("$[0].type").value("transfer"))
                .andExpect(jsonPath("$[0].role").value("receiver"))
                .andExpect(jsonPath("$[0].deposit").value(125000.0))
                .andExpect(jsonPath("$[1].id").value(1201L))
                .andExpect(jsonPath("$[1].type").value("transfer"))
                .andExpect(jsonPath("$[1].role").value("sender"))
                .andExpect(jsonPath("$[1].deposit").value(-25000.0))
                .andExpect(jsonPath("$[2].id").value(1101L))
                .andExpect(jsonPath("$[2].type").value("deposit"));
    }

    @Test
    void testGetAllAccountOperationWhenSortIsDescShouldReturnList() throws Exception {
        Account a1 = Account.builder()
                .id(1001L)
                .name("Alice")
                .passwordHash("password_hash1")
                .build();

        Account a2 = Account.builder()
                .id(1002L)
                .name("Bob")
                .passwordHash("password_hash2")
                .build();

        AccountDepositOperation depositOperation = AccountDepositOperation.builder()
                .id(1101L)
                .account(a1)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation1 = AccountTransferOperation.builder()
                .id(1201L)
                .sender(a1)
                .receiver(a2)
                .deposit(25000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation2 = AccountTransferOperation.builder()
                .id(1202L)
                .sender(a2)
                .receiver(a1)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 3, 0, 0, 0))
                .build();

        when(accountService.findById(1001L)).thenReturn(a1);
        when(accountDepositOperationService.findAllByAccountId(1001L))
                .thenReturn(Collections.singletonList(depositOperation));
        when(accountTransferOperationService.findAllBySenderId(1001L))
                .thenReturn(Collections.singletonList(transferOperation1));
        when(accountTransferOperationService.findAllByReceiverId(1001L))
                .thenReturn(Collections.singletonList(transferOperation2));

        mockMvc.perform(get("/account/1001/operation/all?sort=desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1202L))
                .andExpect(jsonPath("$[0].type").value("transfer"))
                .andExpect(jsonPath("$[0].role").value("receiver"))
                .andExpect(jsonPath("$[0].deposit").value(125000.0))
                .andExpect(jsonPath("$[1].id").value(1201L))
                .andExpect(jsonPath("$[1].type").value("transfer"))
                .andExpect(jsonPath("$[1].role").value("sender"))
                .andExpect(jsonPath("$[1].deposit").value(-25000.0))
                .andExpect(jsonPath("$[2].id").value(1101L))
                .andExpect(jsonPath("$[2].type").value("deposit"));
    }

    @Test
    void testGetAllAccountOperationsByDateSpanShouldReturnList() throws Exception {
        Account account1 = Account.builder()
                .id(1001L)
                .name("Alice")
                .passwordHash("password_hash1")
                .build();
        Account account2 = Account.builder()
                .id(1002L)
                .name("Bob")
                .passwordHash("password_hash2")
                .build();

        AccountDepositOperation depositOperation = AccountDepositOperation.builder()
                .id(1101L)
                .account(account1)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation1 = AccountTransferOperation.builder()
                .id(1201L)
                .sender(account1)
                .receiver(account2)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();
        AccountTransferOperation transferOperation2 = AccountTransferOperation.builder()
                .id(1202L)
                .sender(account2)
                .receiver(account1)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        when(accountService.findById(1001L))
                .thenReturn(account1);
        when(accountDepositOperationService.findAllByAccountIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(depositOperation));
        when(accountTransferOperationService.findAllBySenderIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(transferOperation1));
        when(accountTransferOperationService.findAllByReceiverIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(transferOperation2));

        mockMvc.perform(get(String.format("/account/1001/operation/all?from=%s&to=%s", from, to)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1201L))
                .andExpect(jsonPath("$[0].type").value("transfer"))
                .andExpect(jsonPath("$[0].role").value("sender"))
                .andExpect(jsonPath("$[0].deposit").value(-125000.0))
                .andExpect(jsonPath("$[1].id").value(1202L))
                .andExpect(jsonPath("$[1].type").value("transfer"))
                .andExpect(jsonPath("$[1].role").value("receiver"))
                .andExpect(jsonPath("$[1].deposit").value(125000.0))
                .andExpect(jsonPath("$[2].id").value(1101L))
                .andExpect(jsonPath("$[2].type").value("deposit"))
                .andExpect(jsonPath("$[2].deposit").value(250000.0));
    }

    @Test
    void testGetAllAccountOperationsByDateSpanAndAscSortShouldReturnList() throws Exception {
        Account account1 = Account.builder()
                .id(1001L)
                .name("Alice")
                .passwordHash("password_hash1")
                .build();
        Account account2 = Account.builder()
                .id(1002L)
                .name("Bob")
                .passwordHash("password_hash2")
                .build();

        AccountDepositOperation depositOperation = AccountDepositOperation.builder()
                .id(1101L)
                .account(account1)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build();

        AccountTransferOperation transferOperation1 = AccountTransferOperation.builder()
                .id(1201L)
                .sender(account1)
                .receiver(account2)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();
        AccountTransferOperation transferOperation2 = AccountTransferOperation.builder()
                .id(1202L)
                .sender(account2)
                .receiver(account1)
                .deposit(125000.0)
                .updatedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        when(accountService.findById(1001L))
                .thenReturn(account1);
        when(accountDepositOperationService.findAllByAccountIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(depositOperation));
        when(accountTransferOperationService.findAllBySenderIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(transferOperation1));
        when(accountTransferOperationService.findAllByReceiverIdAndDateSpan(1001L, from, to))
                .thenReturn(Collections.singletonList(transferOperation2));

        mockMvc.perform(get(String.format("/account/1001/operation/all?from=%s&to=%s&sort=asc", from, to)))
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

    @Test
    void testGetAllAccountOperationsWhenAccountDoesNotExistsShouldReturnObject() throws Exception {
        mockMvc.perform(get("/account/1/operation/all"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Unknown account with id 1"));
    }
}