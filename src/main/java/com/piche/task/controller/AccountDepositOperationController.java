package com.piche.task.controller;

import com.piche.task.controller.dto.AccountOperationDTO;
import com.piche.task.model.Account;
import com.piche.task.service.AccountDepositOperationService;
import com.piche.task.service.AccountService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
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
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown account with id %d", id))
                    .toMap());
        }

        return ResponseEntity.ok().body(operationService.findAllByAccountId(id));
    }

    @PostMapping(value = "account/{id}/operation/deposit")
    @Transactional
    public ResponseEntity<Object> saveOperation(@PathVariable("id") Long id,
                                                @RequestBody AccountOperationDTO operation) {
        Account account = accountService.findById(id);

        if (account == null) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown account with id %d", id))
                    .toMap());
        }

        if (operation.getDeposit() == 0) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", "Can`t add operation: deposit value 0")
                    .toMap());
        }

        if (account.getBalance() + operation.getDeposit() < 0) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", "Can`t add operation: account balance can`t become negative")
                    .toMap());
        }

        return ResponseEntity.ok(operationService.save(id, operation.getDeposit()));
    }
}
