package com.piche.task.repository;

import com.piche.task.model.AccountTransferOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountTransferOperationRepository extends JpaRepository<AccountTransferOperation, Long> {

    @Query("SELECT o FROM AccountTransferOperation o WHERE o.sender.id = ?1")
    List<AccountTransferOperation> findAllBySenderId(Long id);

    @Query("SELECT o FROM AccountTransferOperation o WHERE o.receiver.id = ?1")
    List<AccountTransferOperation> findAllByReceiverId(Long id);

    @Query("SELECT o FROM AccountTransferOperation o WHERE o.sender.id = ?1 AND o.updatedAt >= ?2 AND o.updatedAt < ?3")
    List<AccountTransferOperation> findAllBySenderIdAndDateSpan(Long id, LocalDateTime from, LocalDateTime to);

    @Query("SELECT o FROM AccountTransferOperation o WHERE o.receiver.id = ?1 AND o.updatedAt >= ?2 AND o.updatedAt < ?3")
    List<AccountTransferOperation> findAllByReceiverIdAndDateSpan(Long id, LocalDateTime from, LocalDateTime to);
}
