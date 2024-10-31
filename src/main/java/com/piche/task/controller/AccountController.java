package com.piche.task.controller;

import com.piche.task.dto.AccountDTO;
import com.piche.task.dto.AccountOperationResponseDTO;
import com.piche.task.model.Account;
import com.piche.task.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "account")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping(value = "account/{id}")
    public Account getAccount(@PathVariable("id") Long id) {
        return accountService.findById(id);
    }

    @GetMapping(value = "account", params = "name")
    public Account getAccountByName(@RequestParam("name") String name) {
        return accountService.findByName(name);
    }

    @PostMapping(value = "account")
    public Account saveAccount(@RequestBody AccountDTO account) {
        return accountService.save(account);
    }

    @PostMapping(value = "account/validate")
    public void validateAccount(@RequestBody AccountDTO account) {
        accountService.validate(account);
    }

    @DeleteMapping(value = "account/{id}")
    public void deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @GetMapping(value = "account/{id}/operation/all")
    public List<AccountOperationResponseDTO> getAllAccountOperations(@PathVariable("id") Long id,
                                                                     @RequestParam(value = "sort", required = false) String sort) {
        return accountService.getAllOperations(id, sort);
    }

    @GetMapping(value = "account/{id}/operation/all", params = {"from", "to"})
    public List<AccountOperationResponseDTO> getAllAccountOperationsByDateSpan(@PathVariable("id") Long id,
                                                          @RequestParam("from") LocalDate from,
                                                          @RequestParam("to") LocalDate to,
                                                          @RequestParam(value = "sort", required = false) String sort) {
        return accountService.getAllOperationsByDateSpan(id, from, to, sort);
    }
}
