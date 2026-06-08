package com.stano.gradle;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RootExtensionTest {
    @Test
    void shouldBeAbleToGetAndSetDataFromRootExtension() {
        RootExtension rootExtension = new RootExtension();
        rootExtension.setPactBrokerUrl("pactBrokerUrl");
        rootExtension.setPactBrokerUsername("pactBrokerUsername");
        rootExtension.setPactBrokerPassword("pactBrokerPassword");
        rootExtension.setPactBrokerToken("pactBrokerToken");
        rootExtension.setJavaVersion("javaVersion");
        rootExtension.setContextName("contextName");
        rootExtension.setBuildTime(LocalDateTime.of(2022, 10, 6, 8, 30, 45));

        assertEquals("pactBrokerUrl", rootExtension.getPactBrokerUrl());
        assertEquals("pactBrokerUsername", rootExtension.getPactBrokerUsername());
        assertEquals("pactBrokerPassword", rootExtension.getPactBrokerPassword());
        assertEquals("pactBrokerToken", rootExtension.getPactBrokerToken());
        assertEquals("javaVersion", rootExtension.getJavaVersion());
        assertEquals("contextName", rootExtension.getContextName());
        assertEquals(LocalDateTime.of(2022, 10, 6, 8, 30, 45), rootExtension.getBuildTime());
        assertEquals("20221006083045", rootExtension.getBuildTimeFormatted());
    }
}
