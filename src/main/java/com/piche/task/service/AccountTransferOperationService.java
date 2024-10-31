package com.piche.task.service;

import com.piche.task.dto.AccountOperationDTO;
import com.piche.task.exception.BadRequestException;
import com.piche.task.exception.UnknownAccountIdException;
import com.piche.task.model.Account;
import com.piche.task.model.AccountTransferOperation;
import com.piche.task.repository.AccountRepository;
import com.piche.task.repository.AccountTransferOperationRepository;
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
public class AccountTransferOperationService {

    private final AccountRepository accountRepository;

    private final AccountTransferOperationRepository transferOperationRepository;

    private final IdGenerator generator;

    @PersistenceContext
    private final EntityManager manager;

    public List<AccountTransferOperation> findAllBySenderId(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return transferOperationRepository.findAllBySenderId(id);
    }

    public List<AccountTransferOperation> findAllBySenderIdAndDateSpan(Long id, LocalDate from, LocalDate to) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return transferOperationRepository.findAllBySenderIdAndDateSpan(id, from.atStartOfDay(), to.atStartOfDay());
    }

    public List<AccountTransferOperation> findAllByReceiverId(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return transferOperationRepository.findAllByReceiverId(id);
    }

    public List<AccountTransferOperation> findAllByReceiverIdAndDateSpan(Long id, LocalDate from, LocalDate to) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        return transferOperationRepository.findAllByReceiverIdAndDateSpan(id, from.atStartOfDay(), to.atStartOfDay());
    }

    @Transactional
    public AccountTransferOperation save(long senderId, long receiverId, AccountOperationDTO operation) {
        Account sender = accountRepository.findById(senderId).orElseThrow(() ->
                new BadRequestException(String.format("Unknown sender account with id %d", senderId)));

        if (!accountRepository.existsById(receiverId)) {
            throw new BadRequestException(String.format("Unknown receiver account with id %d", receiverId));
        }

        if (operation.getDeposit() <= 0) {
            throw new BadRequestException("Can`t add operation: deposit value can`t be negative or zero");
        }

        if (sender.getBalance() - operation.getDeposit() < 0) {
            throw new BadRequestException("Can`t add operation: account balance can`t become negative");
        }

        long id = generator.generateId().getLeastSignificantBits();

        manager.createNativeQuery(
                        "INSERT INTO transfer_operation (id, sender_id, receiver_id, updated_at, deposit) " +
                                "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, senderId)
                .setParameter(3, receiverId)
                .setParameter(4, LocalDateTime.now())
                .setParameter(5, operation.getDeposit())
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance + ? WHERE a.id = ?")
                .setParameter(1, operation.getDeposit())
                .setParameter(2, receiverId)
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance - ? WHERE a.id = ?")
                .setParameter(1, operation.getDeposit())
                .setParameter(2, senderId)
                .executeUpdate();

        manager.flush();
        manager.clear();

        return transferOperationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
