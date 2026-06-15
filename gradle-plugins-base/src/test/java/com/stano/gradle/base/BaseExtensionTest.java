package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BaseExtensionTest {
  @Test
  void shouldBeAbleToGetAndSetDataFromBaseExtension() {
    BaseExtension rootExtension = new BaseExtension();
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
