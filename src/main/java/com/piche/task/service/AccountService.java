package com.piche.task.service;

import com.piche.task.model.Account;
import com.piche.task.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public List<Account> findAll() {
        return repository.findAll();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Account findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Account findByName(String name) {
        return repository.findByName(name).orElse(null);
    }

    public Account save(Account account) {
        return repository.save(account);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
