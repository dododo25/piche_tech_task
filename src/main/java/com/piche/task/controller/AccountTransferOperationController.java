package com.piche.task.controller;

import com.piche.task.controller.dto.AccountOperationDTO;
import com.piche.task.exception.BadRequestException;
import com.piche.task.model.Account;
import com.piche.task.service.AccountService;
import com.piche.task.service.AccountTransferOperationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AccountTransferOperationController {

    private final AccountService accountService;

    private final AccountTransferOperationService operationService;

    @GetMapping(value = "account/{id}/operation/transfer")
    public ResponseEntity<Object> getAllOperations(@PathVariable("id") Long id) {
        if (!accountService.existsById(id)) {
            throw new BadRequestException(String.format("Unknown account with id %d", id));
        }

        return ResponseEntity.ok(operationService.findAllBySenderId(id));
    }

    @PostMapping(value = "account/{senderId}/operation/transfer/{receiverId}")
    public ResponseEntity<Object> saveOperation(@PathVariable("senderId") Long senderId,
                                                @PathVariable("receiverId") Long receiverId,
                                                @RequestBody AccountOperationDTO operation) {
        Account sender = accountService.findById(senderId);

        if (sender == null) {
            throw new BadRequestException(String.format("Unknown sender account with id %d", senderId));
        }

        if (!accountService.existsById(receiverId)) {
            throw new BadRequestException(String.format("Unknown receiver account with id %d", receiverId));
        }

        if (operation.getDeposit() <= 0) {
            throw new BadRequestException("Can`t add operation: deposit value 0");
        }

        if (sender.getBalance() - operation.getDeposit() < 0) {
            throw new BadRequestException("Can`t add operation: account balance can`t become negative");
        }

        return ResponseEntity.ok(operationService.save(senderId, receiverId, operation.getDeposit()));
    }
}
