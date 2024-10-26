package com.piche.task.service;

import com.piche.task.model.AccountTransferOperation;
import com.piche.task.repository.AccountTransferOperationRepository;
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
public class AccountTransferOperationService {

    private final AccountTransferOperationRepository repository;

    private final IdGenerator generator;

    @PersistenceContext
    private final EntityManager manager;

    public List<AccountTransferOperation> findAllBySenderId(Long id) {
        return repository.findAllBySenderId(id);
    }

    public List<AccountTransferOperation> findAllByReceiverId(Long id) {
        return repository.findAllByReceiverId(id);
    }

    @Transactional
    public AccountTransferOperation save(long senderId, long receiverId, double deposit) {
        long id = generator.generateId().getLeastSignificantBits();

        manager.createNativeQuery(
                        "INSERT INTO transfer_operation (id, sender_id, receiver_id, updated_at, deposit) " +
                        "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, senderId)
                .setParameter(3, receiverId)
                .setParameter(4, LocalDateTime.now())
                .setParameter(5, deposit)
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance + ? WHERE a.id = ?")
                .setParameter(1, deposit)
                .setParameter(2, receiverId)
                .executeUpdate();
        manager.createNativeQuery("UPDATE account a SET a.balance = a.balance - ? WHERE a.id = ?")
                .setParameter(1, deposit)
                .setParameter(2, senderId)
                .executeUpdate();

        manager.flush();
        manager.clear();

        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
