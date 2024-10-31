package com.piche.task.controller;

import com.piche.task.model.Account;
import com.piche.task.model.AccountTransferOperation;
import com.piche.task.service.AccountTransferOperationService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AccountTransferOperationController.class})
class AccountTransferOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountTransferOperationService accountTransferOperationService;

    @Test
    void testGetAllOperationsShouldReturnList() throws Exception {
        Account sender = mock();
        Account receiver = mock();

        AccountTransferOperation o1 = AccountTransferOperation.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .deposit(250000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        AccountTransferOperation o2 = AccountTransferOperation.builder()
                .id(2L)
                .sender(sender)
                .receiver(receiver)
                .deposit(-125000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountTransferOperationService.findAllBySenderId(1L)).thenReturn(Arrays.asList(o1, o2));

        mockMvc.perform(get("/account/1/operation/transfer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(o1.getId()))
                .andExpect(jsonPath("$[1].id").value(o2.getId()));
    }

    @Test
    void testSaveOperationShouldReturnObject() throws Exception {
        when(accountTransferOperationService.save(any(long.class), any(long.class), any())).thenReturn(mock());

        mockMvc.perform(post("/account/1/operation/transfer/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject()
                                .put("deposit", 250000)
                                .toString()))
                .andExpect(status().isOk());
    }
}