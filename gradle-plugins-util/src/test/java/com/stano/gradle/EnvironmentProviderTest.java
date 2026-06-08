package com.stano.gradle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnvironmentProviderTest {
    @Test
    void defaultEnvironmentShouldBeSystemEnvironment() {
        assertInstanceOf(SystemEnvironment.class, EnvironmentProvider.getEnvironment());
    }

    @Test
    void shouldBeAbleToSetDifferentEnvironmentObject() {
        Environment mockEnv = mock(Environment.class);
        when(mockEnv.getEnvironmentVariable("TEST")).thenReturn("test_value");

        EnvironmentProvider.setEnvironment(mockEnv);

        assertEquals("test_value", EnvironmentProvider.getEnvironment().getEnvironmentVariable("TEST"));
    }

    @AfterEach
    void resetEnvironment() {
        EnvironmentProvider.reset();
    }

    @Test
    void shouldBeAbleToUseEnvironmentProvider() {
        EnvironmentProvider.getEnvironment();
    }
}
