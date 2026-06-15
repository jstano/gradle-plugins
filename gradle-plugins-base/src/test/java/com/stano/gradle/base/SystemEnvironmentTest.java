package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SystemEnvironmentTest {
  @Test
  void getAllEnvironmentVariablesShouldReturnAllValuesFromSystemGetenv() {
    SystemEnvironment systemEnvironment = new SystemEnvironment();
    var all = systemEnvironment.getAllEnvironmentVariables();
    assertEquals(System.getenv(), all);
  }

  @Test
  void getEnvironmentVariableShouldReturnValueUsingSystemGetenv() {
    SystemEnvironment systemEnvironment = new SystemEnvironment();
    String firstKey = System.getenv().keySet().iterator().next();
    assertNull(systemEnvironment.getEnvironmentVariable("TEST"));
    assertEquals(System.getenv(firstKey), systemEnvironment.getEnvironmentVariable(firstKey));
  }
}
