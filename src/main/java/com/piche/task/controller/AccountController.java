package com.piche.task.controller;

import com.piche.task.controller.dto.AccountDTO;
import com.piche.task.encoder.PasswordEncoder;
import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.model.AccountTransferOperation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

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
    public ResponseEntity<Object> getAllAccountOperations(@PathVariable("id") Long id,
                                                          @RequestParam(value = "sort", required = false) String sort) {
        return prepareAccountOperations(
                id,
                sort,
                () -> depositOperationService.findAllByAccountId(id),
                () -> transferOperationService.findAllBySenderId(id),
                () -> transferOperationService.findAllByReceiverId(id));
    }

    @GetMapping(value = "account/{id}/operation/all", params = {"from", "to"})
    public ResponseEntity<Object> getAllAccountOperationsByDateSpan(@PathVariable("id") Long id,
                                                                    @RequestParam("from") LocalDate from,
                                                                    @RequestParam("to") LocalDate to,
                                                                    @RequestParam(value = "sort", required = false) String sort) {
        return prepareAccountOperations(
                id,
                sort,
                () -> depositOperationService.findAllByAccountIdAndDateSpan(id, from, to),
                () -> transferOperationService.findAllBySenderIdAndDateSpan(id, from, to),
                () -> transferOperationService.findAllByReceiverIdAndDateSpan(id, from, to));
    }

    private ResponseEntity<Object> prepareAccountOperations(Long id,
                                                            String sort,
                                                            Supplier<List<AccountDepositOperation>> depositsSupplier,
                                                            Supplier<List<AccountTransferOperation>> firstTransfersSupplier,
                                                            Supplier<List<AccountTransferOperation>> secondTransfersSupplier) {
        try {
            invalidateAccount(id);

            Map<LocalDateTime, List<Object>> result = prepareTreeMap(sort);

            depositsSupplier.get().forEach(operation -> {
                LocalDateTime time = operation.getUpdatedAt();

                result.computeIfAbsent(time, key -> new ArrayList<>()).add(new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "deposit")
                        .put("deposit", operation.getDeposit())
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap());
            });
            firstTransfersSupplier.get().forEach(operation -> {
                LocalDateTime time = operation.getUpdatedAt();

                result.computeIfAbsent(time, key -> new ArrayList<>()).add(new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "transfer")
                        .put("role", "sender")
                        .put("deposit", operation.getDeposit() * -1)
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap());
            });
            secondTransfersSupplier.get().forEach(operation -> {
                LocalDateTime time = operation.getUpdatedAt();

                result.computeIfAbsent(time, key -> new ArrayList<>()).add(new JSONObject()
                        .put("id", operation.getId())
                        .put("type", "transfer")
                        .put("role", "receiver")
                        .put("deposit", operation.getDeposit())
                        .put("updatedAt", operation.getUpdatedAt())
                        .toMap());
            });

            return ResponseEntity.ok(result.values().stream().flatMap(Collection::stream).toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", e.getMessage())
                    .toMap());
        }
    }

    private void invalidateAccount(Long id) {
        Account account = accountService.findById(id);

        if (account == null) {
            throw new IllegalArgumentException(String.format("Unknown account with id %d", id));
        }
    }

    private static Map<LocalDateTime, List<Object>> prepareTreeMap(String sort) {
        if (sort == null) {
            return new TreeMap<>(Comparator.reverseOrder());
        }

        return switch (sort.toLowerCase()) {
            case "asc" -> new TreeMap<>(Comparator.naturalOrder());
            case "desc" -> new TreeMap<>(Comparator.reverseOrder());
            default -> throw new IllegalArgumentException(String.format("Unknown sort type '%s'", sort));
        };
    }
}
