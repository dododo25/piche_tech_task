package com.piche.task.service;

import com.piche.task.dto.AccountOperationDTO;
import com.piche.task.exception.BadRequestException;
import com.piche.task.exception.UnknownAccountIdException;
import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.repository.AccountDepositOperationRepository;
import com.piche.task.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountDepositOperationService {

    private final AccountRepository accountRepository;

    private final AccountDepositOperationRepository depositOperationRepository;

    private final IdGenerator generator;

    @PersistenceContext
    private final EntityManager manager;

    public List<AccountDepositOperation> findAllByAccountId(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return depositOperationRepository.findAllByAccountId(id);
    }

    public List<AccountDepositOperation> findAllByAccountIdAndDateSpan(Long id, LocalDate from, LocalDate to) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return depositOperationRepository.findAllByAccountIdAndDateSpan(id, from.atStartOfDay(), to.atStartOfDay());
    }

    @Transactional
    public AccountDepositOperation save(long accountId, AccountOperationDTO operation) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new UnknownAccountIdException(accountId));

        if (operation.getDeposit() == 0) {
            throw new BadRequestException("Can`t add operation: deposit value 0");
        }

        if (account.getBalance() + operation.getDeposit() < 0) {
            throw new BadRequestException("Can`t add operation: account balance can`t become negative");
        }

        long id = generator.generateId().getLeastSignificantBits();

        manager.createNativeQuery(
                        "INSERT INTO deposit_operation (id, account_id, updated_at, deposit) " +
                        "VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, accountId)
                .setParameter(3, LocalDateTime.now())
                .setParameter(4, operation.getDeposit())
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance + ? WHERE a.id = ?")
                .setParameter(1, operation.getDeposit())
                .setParameter(2, accountId)
                .executeUpdate();

        manager.flush();
        manager.clear();

        return depositOperationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
