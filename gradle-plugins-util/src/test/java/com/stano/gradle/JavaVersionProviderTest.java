package com.stano.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.gradle.api.JavaVersion;
import org.junit.jupiter.api.Test;

class JavaVersionProviderTest {
  @Test
  void currentVersionShouldReturnCurrentJavaVersion() {
    assertEquals(JavaVersion.current(), new JavaVersionProvider().currentVersion());
  }
}
