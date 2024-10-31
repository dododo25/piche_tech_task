package com.piche.task.service;

import com.piche.task.dto.AccountOperationDTO;
import com.piche.task.exception.BadRequestException;
import com.piche.task.exception.UnknownAccountIdException;
import com.piche.task.model.Account;
import com.piche.task.model.AccountDepositOperation;
import com.piche.task.repository.AccountDepositOperationRepository;
import com.piche.task.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountDepositOperationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountDepositOperationRepository depositOperationRepository;

    @Mock
    private EntityManager manager;

    @Mock
    private IdGenerator generator;

    @InjectMocks
    private AccountDepositOperationService service;

    @Test
    void testFindAllByAccountIdShouldReturnObject() {
        AccountDepositOperation o1 = mock();
        AccountDepositOperation o2 = mock();

        when(accountRepository.existsById(1L))
                .thenReturn(true);
        when(depositOperationRepository.findAllByAccountId(1L))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountDepositOperation> result = service.findAllByAccountId(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllByAccountIdWhenAccountNotExistsShouldThrowException() {
        when(accountRepository.existsById(any()))
                .thenReturn(false);

        assertThrows(UnknownAccountIdException.class, () -> service.findAllByAccountId(1L));
    }

    @Test
    void testFindAllByAccountIdAndDateSpanShouldReturnObject() {
        AccountDepositOperation o1 = mock();
        AccountDepositOperation o2 = mock();

        LocalDateTime from = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(2024, 1, 3).atStartOfDay();

        when(accountRepository.existsById(1L)).thenReturn(true);
        when(depositOperationRepository.findAllByAccountIdAndDateSpan(1L, from, to))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountDepositOperation> result =
                service.findAllByAccountIdAndDateSpan(1L, from.toLocalDate(), to.toLocalDate());

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllByAccountIdAndDateSpanWhenAccountNotExistsShouldThrowException() {
        assertThrows(UnknownAccountIdException.class, () -> service.findAllByAccountIdAndDateSpan(1L, null, null));
    }

    @Test
    void testSaveShouldReturnObject() {
        Account mockedAccount = mock();
        AccountOperationDTO mockedOperation = mock();

        Query mockedQuery = mock();

        when(generator.generateId()).thenReturn(new UUID(0L, 1L));
        when(manager.createNativeQuery(any())).thenReturn(mockedQuery);
        when(mockedAccount.getBalance()).thenReturn(0.0);
        when(mockedOperation.getDeposit()).thenReturn(250000.0);
        when(mockedQuery.setParameter(any(int.class), any())).thenReturn(mockedQuery);
        when(mockedQuery.executeUpdate()).thenReturn(1);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockedAccount));
        when(depositOperationRepository.findById(1L)).thenAnswer(invocationOnMock -> {
            AccountDepositOperation result = mock();
            when(result.getId()).thenReturn(1L);
            return Optional.of(result);
        });

        AccountDepositOperation saved = service.save(1L, mockedOperation);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void testSaveWhenAccountDoesNotExistsShouldThrowException() {
        when(accountRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(UnknownAccountIdException.class, () -> service.save(1L, mock()));
    }

    @Test
    void testSaveWhenDepositValueIsZeroShouldThrowException() {
        Account mockedAccount = mock();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(mockedAccount));

        assertThrows(BadRequestException.class, () -> service.save(1L, mock()));
    }

    @Test
    void testSaveWhenDepositValueIsBalanceIsInvalidShouldThrowException() {
        AccountOperationDTO mockedOperation = mock();

        when(mockedOperation.getDeposit()).thenReturn(-250000.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mock()));

        assertThrows(BadRequestException.class, () -> service.save(1L, mockedOperation));
    }
}