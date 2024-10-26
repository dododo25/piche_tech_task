package com.piche.task.controller;

import com.piche.task.controller.dto.AccountOperationDTO;
import com.piche.task.model.Account;
import com.piche.task.service.AccountService;
import com.piche.task.service.AccountTransferOperationService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
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
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown account with id %d", id))
                    .toMap());
        }

        return ResponseEntity.ok(operationService.findAllBySenderId(id));
    }

    @PostMapping(value = "account/{senderId}/operation/transfer/{receiverId}")
    public ResponseEntity<Object> saveOperation(@PathVariable("senderId") Long senderId,
                                                @PathVariable("receiverId") Long receiverId,
                                                @RequestBody AccountOperationDTO operation) {
        Account sender = accountService.findById(senderId);

        if (sender == null) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown sender account with id %d", senderId))
                    .toMap());
        }

        if (!accountService.existsById(receiverId)) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", String.format("Unknown receiver account with id %d", receiverId))
                    .toMap());
        }

        if (operation.getDeposit() <= 0) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", "Can`t add operation: deposit value 0")
                    .toMap());
        }

        if (sender.getBalance() - operation.getDeposit() < 0) {
            return ResponseEntity.badRequest().body(new JSONObject()
                    .put("message", "Can`t add operation: account balance can`t become negative")
                    .toMap());
        }

        return ResponseEntity.ok(operationService.save(senderId, receiverId, operation.getDeposit()));
    }
}
