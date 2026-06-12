package com.stano.gradle;

import org.gradle.api.JavaVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaVersionProviderTest {
  @Test
  void currentVersionShouldReturnCurrentJavaVersion() {
    assertEquals(JavaVersion.current(), new JavaVersionProvider().currentVersion());
  }
}
