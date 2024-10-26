package com.piche.task.service;

import com.piche.task.model.AccountDepositOperation;
import com.piche.task.repository.AccountDepositOperationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.IdGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountDepositOperationServiceTest {

    @Mock
    private AccountDepositOperationRepository repository;

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

        when(repository.findAllByAccountId(1L))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountDepositOperation> result = service.findAllByAccountId(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testSaveShouldReturnObject() {
        Query mockedQuery = mock();

        when(generator.generateId()).thenReturn(new UUID(0L, 1L));
        when(manager.createNativeQuery(any())).thenReturn(mockedQuery);
        when(mockedQuery.setParameter(any(int.class), any())).thenReturn(mockedQuery);
        when(mockedQuery.executeUpdate()).thenReturn(1);
        when(repository.findById(1L)).thenAnswer(invocationOnMock -> {
            AccountDepositOperation result = mock();
            when(result.getId()).thenReturn(1L);
            return Optional.of(result);
        });

        AccountDepositOperation saved = service.save(1L, 250000);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }
}