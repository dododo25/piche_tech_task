package com.piche.task.service;

import com.piche.task.model.AccountTransferOperation;
import com.piche.task.repository.AccountTransferOperationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountTransferOperationServiceTest {

    @Mock
    private AccountTransferOperationRepository repository;

    @Mock
    private EntityManager manager;

    @Mock
    private IdGenerator generator;

    @InjectMocks
    private AccountTransferOperationService service;

    @Test
    void testFindAllBySenderIdShouldReturnObject() {
        AccountTransferOperation o1 = mock();
        AccountTransferOperation o2 = mock();

        when(repository.findAllBySenderId(1L))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountTransferOperation> result = service.findAllBySenderId(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllBySenderIdAndDateSpanShouldReturnObject() {
        AccountTransferOperation o1 = mock();
        AccountTransferOperation o2 = mock();

        LocalDateTime from = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(2024, 1, 3).atStartOfDay();

        when(repository.findAllBySenderIdAndDateSpan(1L, from, to))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountTransferOperation> result =
                service.findAllBySenderIdAndDateSpan(1L, from.toLocalDate(), to.toLocalDate());

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllByReceiverIdShouldReturnObject() {
        AccountTransferOperation o1 = mock();
        AccountTransferOperation o2 = mock();

        when(repository.findAllByReceiverId(1L))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountTransferOperation> result = service.findAllByReceiverId(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindAllByReceiverIdAndDateSpanShouldReturnObject() {
        AccountTransferOperation o1 = mock();
        AccountTransferOperation o2 = mock();

        LocalDateTime from = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(2024, 1, 3).atStartOfDay();

        when(repository.findAllByReceiverIdAndDateSpan(1L, from, to))
                .thenReturn(Arrays.asList(o1, o2));
        when(o1.getId()).thenReturn(1L);
        when(o2.getId()).thenReturn(2L);

        List<AccountTransferOperation> result =
                service.findAllByReceiverIdAndDateSpan(1L, from.toLocalDate(), to.toLocalDate());

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
            AccountTransferOperation result = mock();
            when(result.getId()).thenReturn(1L);
            return Optional.of(result);
        });

        AccountTransferOperation saved = service.save(1L, 2L, 250000);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }
}