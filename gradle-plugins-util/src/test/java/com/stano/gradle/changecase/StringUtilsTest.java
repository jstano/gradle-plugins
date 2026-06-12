package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
  @ParameterizedTest
  @CsvSource({",true", "'',true", "' ',true", "' abc ',false"})
  void isBlankShouldWork(String text, boolean expectedResult) {
    assertEquals(expectedResult, StringUtils.isBlank(text));
  }
}
