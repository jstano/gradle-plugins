package com.stano.gradle.base.changecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UpperCaseTest {
  @ParameterizedTest
  @CsvSource({
    ",",
    "'',''",
    "test,TEST",
    "'test string','TEST STRING'",
    "'Test String','TEST STRING'"
  })
  void upperCase(String text, String result) {
    assertEquals(result, UpperCase.upperCase(text));
  }
}
