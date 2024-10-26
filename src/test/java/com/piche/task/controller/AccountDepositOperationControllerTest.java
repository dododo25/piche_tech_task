package com.piche.task.controller;

import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.service.AccountDepositOperationService;
import com.piche.task.service.AccountService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AccountDepositOperationController.class, EntityManager.class,
        EntityManagerFactory.class})
class AccountDepositOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static EntityManagerFactory factory;

    @MockBean
    private static EntityManager manager;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountDepositOperationService accountDepositOperationService;

    @Test
    void testGetAllOperationsShouldReturnList() throws Exception {
        Account account = mock();

        AccountDepositOperation o1 = AccountDepositOperation.builder()
                .id(1L)
                .account(account)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        AccountDepositOperation o2 = AccountDepositOperation.builder()
                .id(2L)
                .account(account)
                .deposit(-125000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountService.existsById(1L)).thenReturn(true);
        when(accountDepositOperationService.findAllByAccountId(1L)).thenReturn(Arrays.asList(o1, o2));

        mockMvc.perform(get("/account/1/operation/deposit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(o1.getId()))
                .andExpect(jsonPath("$[1].id").value(o2.getId()));
    }

    @Test
    void testGetAllOperationsWhenAccountDoesNotExistsShouldReturnObject() throws Exception {
        when(accountService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/account/2/operation/deposit"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Unknown account with id 2"));
    }

    @Test
    void testSaveOperationShouldReturnObject() throws Exception {
        when(accountService.findById(1L)).thenReturn(mock());

        mockMvc.perform(post("/account/1/operation/deposit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject()
                                .put("deposit", 250000)
                                .toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveOperationWhenAccountDoesNotExistsShouldReturnObject() throws Exception {
        mockMvc.perform(post("/account/1/operation/deposit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject()
                                .put("deposit", 250000)
                                .toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Unknown account with id 1"));
    }

    @Test
    void testSaveOperationWhenDepositValueIsZeroShouldReturnObject() throws Exception {
        when(accountService.findById(1L)).thenReturn(mock());

        mockMvc.perform(post("/account/1/operation/deposit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject()
                                .put("deposit", 0)
                                .toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Can`t add operation: deposit value 0"));
    }

    @Test
    void testSaveOperationWhenNewBalanceIsNegativeShouldReturnObject() throws Exception {
        when(accountService.findById(1L)).thenReturn(mock());

        mockMvc.perform(post("/account/1/operation/deposit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject()
                                .put("deposit", -250000)
                                .toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Can`t add operation: account balance can`t become negative"));
    }
}