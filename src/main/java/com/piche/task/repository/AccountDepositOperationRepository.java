package com.piche.task.repository;

import com.piche.task.model.AccountDepositOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDepositOperationRepository extends JpaRepository<AccountDepositOperation, Long> {

    @Query("SELECT o FROM AccountDepositOperation o WHERE o.account.id = ?1")
    List<AccountDepositOperation> findAllByAccountId(Long id);
}
