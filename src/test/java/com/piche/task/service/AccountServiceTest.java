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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountDepositOperationRepository depositOperationRepository;

    @Mock
    private AccountTransferOperationRepository transferOperationRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AccountService service;

    @Test
    void testFindAllShouldReturnList() {
        Account a1 = mock();
        Account a2 = mock();

        when(accountRepository.findAll()).thenReturn(Arrays.asList(a1, a2));
        when(a1.getId()).thenReturn(1L);
        when(a2.getId()).thenReturn(2L);

        List<Account> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testExistsByIdShouldReturnBoolean() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        assertTrue(service.existsById(1L));
        assertFalse(service.existsById(2L));
    }

    @Test
    void testFindByIdShouldReturnObject() {
        Account account = mock();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(account.getId()).thenReturn(1L);

        Account result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdWhenAccountDoesNotExistsShouldThrowException() {
        when(accountRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(UnknownAccountIdException.class, () -> service.findById(1L));
    }

    @Test
    void testFindByNameShouldReturnObject() {
        Account account = mock();

        when(accountRepository.findByName("test")).thenReturn(Optional.of(account));
        when(account.getId()).thenReturn(1L);

        Account result = service.findByName("test");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByNameWhenAccountDoesNotExistsShouldThrowException() {
        when(accountRepository.findByName(any()))
                .thenReturn(Optional.empty());

        assertThrows(UnknownAccountNameException.class, () -> service.findByName("test"));
    }

    @Test
    void testSaveShouldReturnObject() {
        AccountDTO mockedAccount = mock();

        when(accountRepository.save(any())).thenAnswer(invocationOnMock -> {
            Account result = mock();
            when(result.getId()).thenReturn(1L);
            return result;
        });

        Account saved = service.save(mockedAccount);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void testSaveWhenAccountAlreadyExistsShouldThrowException() {
        AccountDTO mockedAccount = mock();

        when(mockedAccount.getName()).thenReturn("Alice");
        when(accountRepository.existsByName(any())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.save(mockedAccount));
    }

    @Test
    void testValidateShouldDoneWell() {
        Account account = mock();

        when(accountRepository.findByName(any())).thenReturn(Optional.of(account));
        when(account.getPasswordHash()).thenReturn("password_hash");
        when(encoder.encode(any())).thenReturn("password_hash");

        service.validate(mock());
    }

    @Test
    void testValidateWhenAccountNotExistsShouldThrowException() {
        when(accountRepository.findByName(any()))
                .thenReturn(Optional.empty());

        assertThrows(UnknownAccountNameException.class, () -> service.validate(mock()));
    }

    @Test
    void testValidateWhenPasswordIsWrongShouldThrowException() {
        Account account = mock();

        when(accountRepository.findByName(any())).thenReturn(Optional.of(account));
        when(account.getPasswordHash()).thenReturn("password_hash");

        assertThrows(BadRequestException.class, () -> service.validate(mock()));
    }

    @Test
    void testGetAllOperationsShouldReturnList() {
        AccountDepositOperation o1 = mock();
        AccountTransferOperation o2 = mock();
        AccountTransferOperation o3 = mock();

        when(o1.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(o2.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 3, 0, 0, 0));
        when(o3.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(depositOperationRepository.findAllByAccountId(1L)).thenReturn(Collections.singletonList(o1));
        when(transferOperationRepository.findAllBySenderId(1L)).thenReturn(Collections.singletonList(o2));
        when(transferOperationRepository.findAllByReceiverId(1L)).thenReturn(Collections.singletonList(o3));

        List<AccountOperationResponseDTO> operations = service.getAllOperations(1L, null);

        assertEquals(3, operations.size());
        assertEquals(3, operations.get(0).getUpdatedAt().getDayOfMonth());
        assertEquals(2, operations.get(1).getUpdatedAt().getDayOfMonth());
        assertEquals(1, operations.get(2).getUpdatedAt().getDayOfMonth());
    }

    @Test
    void testGetAllOperationsWhenSortIsIllegalShouldThrowException() {
        when(accountRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.getAllOperations(1L, "wrong"));
    }

    @Test
    void testGetAllOperationsWhenSortIsAscShouldReturnList() {
        AccountDepositOperation o1 = mock();
        AccountTransferOperation o2 = mock();
        AccountTransferOperation o3 = mock();

        when(o1.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(o2.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 3, 0, 0, 0));
        when(o3.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(depositOperationRepository.findAllByAccountId(1L)).thenReturn(Collections.singletonList(o1));
        when(transferOperationRepository.findAllBySenderId(1L)).thenReturn(Collections.singletonList(o2));
        when(transferOperationRepository.findAllByReceiverId(1L)).thenReturn(Collections.singletonList(o3));

        List<AccountOperationResponseDTO> operations = service.getAllOperations(1L, "asc");

        assertEquals(3, operations.size());
        assertEquals(1, operations.get(0).getUpdatedAt().getDayOfMonth());
        assertEquals(2, operations.get(1).getUpdatedAt().getDayOfMonth());
        assertEquals(3, operations.get(2).getUpdatedAt().getDayOfMonth());
    }

    @Test
    void testGetAllOperationsByDateSpanShouldReturnList() {
        AccountDepositOperation o1 = mock();
        AccountTransferOperation o2 = mock();
        AccountTransferOperation o3 = mock();

        when(o1.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        when(o2.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 3, 0, 0, 0));
        when(o3.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(depositOperationRepository.findAllByAccountIdAndDateSpan(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(o1));
        when(transferOperationRepository.findAllBySenderIdAndDateSpan(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(o2));
        when(transferOperationRepository.findAllByReceiverIdAndDateSpan(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(o3));

        List<AccountOperationResponseDTO> operations = service.getAllOperationsByDateSpan(
                1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 4), null);

        assertEquals(3, operations.size());
        assertEquals(3, operations.get(0).getUpdatedAt().getDayOfMonth());
        assertEquals(2, operations.get(1).getUpdatedAt().getDayOfMonth());
        assertEquals(1, operations.get(2).getUpdatedAt().getDayOfMonth());
    }
}