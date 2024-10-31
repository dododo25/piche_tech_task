package com.piche.task.service;

import com.piche.task.dto.AccountDTO;
import com.piche.task.dto.AccountOperationResponseDTO;
import com.piche.task.encoder.PasswordEncoder;
import com.piche.task.exception.BadRequestException;
import com.piche.task.exception.UnknownAccountIdException;
import com.piche.task.exception.UnknownAccountNameException;
import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.model.AccountTransferOperation;
import com.piche.task.repository.AccountDepositOperationRepository;
import com.piche.task.repository.AccountRepository;
import com.piche.task.repository.AccountTransferOperationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountDepositOperationRepository depositOperationRepository;

    private final AccountTransferOperationRepository transferOperationRepository;

    private final PasswordEncoder encoder;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public boolean existsById(Long id) {
        return accountRepository.existsById(id);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new UnknownAccountIdException(id));
    }

    public Account findByName(String name) {
        return accountRepository.findByName(name).orElseThrow(() -> new UnknownAccountNameException(name));
    }

    public List<AccountOperationResponseDTO> getAllOperations(Long id, String sort) {
        return prepareAccountOperations(
                id,
                sort,
                () -> depositOperationRepository.findAllByAccountId(id),
                () -> transferOperationRepository.findAllBySenderId(id),
                () -> transferOperationRepository.findAllByReceiverId(id));
    }

    public List<AccountOperationResponseDTO> getAllOperationsByDateSpan(Long id, LocalDate from, LocalDate to, String sort) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atStartOfDay();

        return prepareAccountOperations(
                id,
                sort,
                () -> depositOperationRepository.findAllByAccountIdAndDateSpan(id, fromDateTime, toDateTime),
                () -> transferOperationRepository.findAllBySenderIdAndDateSpan(id, fromDateTime, toDateTime),
                () -> transferOperationRepository.findAllByReceiverIdAndDateSpan(id, fromDateTime, toDateTime));
    }

    public Account save(AccountDTO account) {
        if (accountRepository.existsByName(account.getName())) {
            throw new BadRequestException(String.format("Account with name '%s' already exists", account.getName()));
        }

        return accountRepository.save(Account.builder()
                .name(account.getName())
                .passwordHash(encoder.encode(account.getPassword()))
                .balance(0.0)
                .build());
    }

    public void validate(AccountDTO account) {
        Account existing = accountRepository.findByName(account.getName()).orElseThrow(() ->
                new UnknownAccountNameException(account.getName()));

        String passwordHash = encoder.encode(account.getPassword());

        if (!existing.getPasswordHash().equals(passwordHash)) {
            throw new BadRequestException("Wrong password");
        }
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    private List<AccountOperationResponseDTO> prepareAccountOperations(Long id,
                                                  String sort,
                                                  Supplier<List<AccountDepositOperation>> depositsSupplier,
                                                  Supplier<List<AccountTransferOperation>> firstTransfersSupplier,
                                                  Supplier<List<AccountTransferOperation>> secondTransfersSupplier) {
        if (!accountRepository.existsById(id)) {
            throw new UnknownAccountIdException(id);
        }

        Map<LocalDateTime, List<AccountOperationResponseDTO>> result = prepareTreeMap(sort);

        depositsSupplier.get().forEach(operation -> {
            LocalDateTime time = operation.getUpdatedAt();

            result.computeIfAbsent(time, key -> new ArrayList<>()).add(AccountOperationResponseDTO.builder()
                    .id(operation.getId())
                    .type("deposit")
                    .deposit(operation.getDeposit())
                    .updatedAt(operation.getUpdatedAt())
                    .build());
        });
        firstTransfersSupplier.get().forEach(operation -> {
            LocalDateTime time = operation.getUpdatedAt();

            result.computeIfAbsent(time, key -> new ArrayList<>()).add(AccountOperationResponseDTO.builder()
                    .id(operation.getId())
                    .type("transfer")
                    .role("sender")
                    .deposit(operation.getDeposit() * -1)
                    .updatedAt(operation.getUpdatedAt())
                    .build());
        });
        secondTransfersSupplier.get().forEach(operation -> {
            LocalDateTime time = operation.getUpdatedAt();

            result.computeIfAbsent(time, key -> new ArrayList<>()).add(AccountOperationResponseDTO.builder()
                    .id(operation.getId())
                    .type("transfer")
                    .role("receiver")
                    .deposit(operation.getDeposit())
                    .updatedAt(operation.getUpdatedAt())
                    .build());
        });

        return result.values().stream().flatMap(Collection::stream).toList();
    }

    private static Map<LocalDateTime, List<AccountOperationResponseDTO>> prepareTreeMap(String sort) {
        if (sort == null) {
            return new TreeMap<>(Comparator.reverseOrder());
        }

        return switch (sort.toLowerCase()) {
            case "asc" -> new TreeMap<>(Comparator.naturalOrder());
            case "desc" -> new TreeMap<>(Comparator.reverseOrder());
            default -> throw new BadRequestException(String.format("Unknown sort type '%s'", sort));
        };
    }
}
