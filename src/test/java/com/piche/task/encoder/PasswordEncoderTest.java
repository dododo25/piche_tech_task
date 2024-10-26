package com.piche.task.encoder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PasswordEncoderTest {

    private static PasswordEncoder encoder;

    @BeforeAll
    static void setup() throws NoSuchAlgorithmException {
        encoder = new PasswordEncoder();
    }

    @Test
    void testEncodeShouldReturnObject() {
        assertNotNull(encoder.encode("PaSSw0rD!"));
    }
}
