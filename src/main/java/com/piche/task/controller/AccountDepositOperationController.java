package com.piche.task.controller;

import com.piche.task.controller.dto.AccountOperationDTO;
import com.piche.task.exception.BadRequestException;
import com.piche.task.model.Account;
import com.piche.task.service.AccountDepositOperationService;
import com.piche.task.service.AccountService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AccountDepositOperationController {

    private final AccountService accountService;

    private final AccountDepositOperationService operationService;

    @PersistenceContext
    private final EntityManager manager;

    @GetMapping(value = "account/{id}/operation/deposit")
    public ResponseEntity<Object> getAllOperations(@PathVariable("id") Long id) {
        if (!accountService.existsById(id)) {
            throw new BadRequestException(String.format("Unknown account with id %d", id));
        }

        return ResponseEntity.ok().body(operationService.findAllByAccountId(id));
    }

    @PostMapping(value = "account/{id}/operation/deposit")
    @Transactional
    public ResponseEntity<Object> saveOperation(@PathVariable("id") Long id,
                                                @RequestBody AccountOperationDTO operation) {
        Account account = accountService.findById(id);

        if (account == null) {
            throw new BadRequestException(String.format("Unknown account with id %d", id));
        }

        if (operation.getDeposit() == 0) {
            throw new BadRequestException("Can`t add operation: deposit value 0");
        }

        if (account.getBalance() + operation.getDeposit() < 0) {
            throw new BadRequestException("Can`t add operation: account balance can`t become negative");
        }

        return ResponseEntity.ok(operationService.save(id, operation.getDeposit()));
    }
}
