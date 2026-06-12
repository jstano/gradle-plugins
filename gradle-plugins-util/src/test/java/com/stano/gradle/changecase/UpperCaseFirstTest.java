package com.stano.gradle.changecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UpperCaseFirstTest {
  @ParameterizedTest
  @CsvSource({",", "'',''", "test,Test", "TEST,TEST"})
  void upperCaseFirst(String text, String result) {
    assertEquals(result, UpperCaseFirst.upperCaseFirst(text));
  }
}
