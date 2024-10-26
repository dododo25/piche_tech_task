package com.piche.task.service;

import com.piche.task.model.Account;
import com.piche.task.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @Test
    void testFindAllShouldReturnList() {
        Account a1 = mock();
        Account a2 = mock();

        when(repository.findAll()).thenReturn(Arrays.asList(a1, a2));
        when(a1.getId()).thenReturn(1L);
        when(a2.getId()).thenReturn(2L);

        List<Account> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindByIdShouldReturnObject() {
        Account account = mock();

        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(account.getId()).thenReturn(1L);

        Account result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByNameShouldReturnObject() {
        Account account = mock();

        when(repository.findByName("test")).thenReturn(Optional.of(account));
        when(account.getId()).thenReturn(1L);

        Account result = service.findByName("test");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testSaveShouldReturnObject() {
        Account account = mock();

        when(repository.save(any())).thenAnswer(invocationOnMock -> {
            Account result = mock();
            when(result.getId()).thenReturn(1L);
            return result;
        });

        Account saved = service.save(account);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void testExistsByIdShouldReturnBoolean() {
        when(repository.existsById(1L)).thenReturn(true);

        assertTrue(service.existsById(1L));
        assertFalse(service.existsById(2L));
    }
}