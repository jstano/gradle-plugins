package com.stano.gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OSTest {
  private static final String ORIGINAL_OS_NAME = System.getProperty("os.name");

  @BeforeEach
  void setup() {
    System.setProperty("os.name", ORIGINAL_OS_NAME);
  }

  private static void cleanup() {
    System.setProperty("os.name", ORIGINAL_OS_NAME);
  }

  @ParameterizedTest
  @CsvSource({"Windows 7, windows", "Mac OSX, mac", "Linux, linux"})
  void nameMethodShouldReturnLowercasedVersionOfOsName(String osName, String expectedResult) {
    System.setProperty("os.name", osName);
    assertEquals(expectedResult, OS.name());
  }

  @ParameterizedTest
  @CsvSource({"Windows 7, true", "Mac OSX, false", "Linux, false"})
  void isWindowsShouldReturnTrueForWindows(String osName, boolean expectedResult) {
    System.setProperty("os.name", osName);
    assertEquals(expectedResult, OS.isWindows());
  }
}
