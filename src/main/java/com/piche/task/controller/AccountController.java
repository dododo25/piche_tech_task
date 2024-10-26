package com.piche.task.controller;

import com.piche.task.controller.dto.AccountDTO;
import com.piche.task.encoder.PasswordEncoder;
import com.piche.task.model.Account;
import com.piche.task.service.AccountDepositOperationService;
import com.piche.task.service.AccountService;
import com.piche.task.service.AccountTransferOperationService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountDepositOperationService depositOperationService;

    private final AccountTransferOperationService transferOperationService;

    private final PasswordEncoder encoder;

    @GetMapping(value = "account")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping(value = "account/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable("id") Long id) {
        Account account = accountService.findById(id);

        if (account == null) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown account with id %d", id))
                    .toMap());
        }

        return ResponseEntity.ok(account);
    }

    @PostMapping(value = "account")
    public ResponseEntity<Object> saveAccount(@RequestBody AccountDTO account) {
        Account existing = accountService.findByName(account.getName());

        if (existing != null) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Account with name '%s' already exists", existing.getName()))
                    .toMap());
        }

        Account saved = accountService.save(Account.builder()
                .name(account.getName())
                .passwordHash(encoder.encode(account.getPassword()))
                .balance(0.0)
                .build());

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping(value = "account/{id}")
    public void deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @GetMapping(value = "account/{id}/operation/all")
    public ResponseEntity<Object> getAllAccountOperations(@PathVariable("id") Long id) {
        Account account = accountService.findById(id);

        if (account == null) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown account with id %d", id))
                    .toMap());
        }

        Map<LocalDateTime, Object> result = new TreeMap<>(Comparator.reverseOrder());

        depositOperationService.findAllByAccountId(id).forEach(operation -> result.put(operation.getUpdatedAt(),
                new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "deposit")
                        .put("deposit", operation.getDeposit())
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap()));
        transferOperationService.findAllBySenderId(id).forEach(operation -> result.put(operation.getUpdatedAt(),
                new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "transfer")
                        .put("role", "sender")
                        .put("deposit", operation.getDeposit() * -1)
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap()));
        transferOperationService.findAllByReceiverId(id).forEach(operation -> result.put(operation.getUpdatedAt(),
                new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "transfer")
                        .put("role", "receiver")
                        .put("deposit", operation.getDeposit())
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap()));

        return ResponseEntity.ok(result.values());
    }
}
