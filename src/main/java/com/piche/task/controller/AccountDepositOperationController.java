package com.piche.task.controller;

import com.piche.task.dto.AccountOperationDTO;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.service.AccountDepositOperationService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AccountDepositOperationController {

    private final AccountDepositOperationService service;

    @GetMapping(value = "account/{id}/operation/deposit")
    public List<AccountDepositOperation> getAllOperations(@PathVariable("id") Long id) {
        return service.findAllByAccountId(id);
    }

    @PostMapping(value = "account/{id}/operation/deposit")
    @Transactional
    public AccountDepositOperation saveOperation(@PathVariable("id") Long id,
                                                 @RequestBody AccountOperationDTO operation) {
        return service.save(id, operation);
    }
}
