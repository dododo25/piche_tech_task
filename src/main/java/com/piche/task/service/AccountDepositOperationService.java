package com.piche.task.service;

import com.piche.task.model.AccountDepositOperation;
import com.piche.task.repository.AccountDepositOperationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountDepositOperationService {

    private final AccountDepositOperationRepository repository;

    private final IdGenerator generator;

    @PersistenceContext
    private final EntityManager manager;

    public List<AccountDepositOperation> findAllByAccountId(Long id) {
        return repository.findAllByAccountId(id);
    }

    @Transactional
    public AccountDepositOperation save(long accountId, double deposit) {
        long id = generator.generateId().getLeastSignificantBits();

        manager.createNativeQuery(
                        "INSERT INTO deposit_operation (id, account_id, updated_at, deposit) " +
                        "VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, accountId)
                .setParameter(3, LocalDateTime.now())
                .setParameter(4, deposit)
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance + ? WHERE a.id = ?")
                .setParameter(1, deposit)
                .setParameter(2, accountId)
                .executeUpdate();

        manager.flush();
        manager.clear();

        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
