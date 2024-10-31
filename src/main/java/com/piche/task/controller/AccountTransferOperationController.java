package com.piche.task.controller;

import com.piche.task.dto.AccountOperationDTO;
import com.piche.task.model.AccountTransferOperation;
import com.piche.task.service.AccountTransferOperationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AccountTransferOperationController {

    private final AccountTransferOperationService operationService;

    @GetMapping(value = "account/{id}/operation/transfer")
    public List<AccountTransferOperation> getAllOperations(@PathVariable("id") Long id) {
        return operationService.findAllBySenderId(id);
    }

    @PostMapping(value = "account/{senderId}/operation/transfer/{receiverId}")
    public AccountTransferOperation saveOperation(@PathVariable("senderId") Long senderId,
                                                  @PathVariable("receiverId") Long receiverId,
                                                  @RequestBody AccountOperationDTO operation) {
        return operationService.save(senderId, receiverId, operation);
    }
}
