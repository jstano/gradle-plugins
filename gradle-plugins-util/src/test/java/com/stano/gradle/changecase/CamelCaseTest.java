package com.stano.gradle.changecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CamelCaseTest {
  @ParameterizedTest
  @CsvSource({
    ",",
    "'',''",
    "test,test",
    "'test string','testString'",
    "'Test String','testString'",
    "'TestV2','testV2'",
    "'_foo_bar_','fooBar'",
    "'version 1.2.10','version_1_2_10'",
    "'version 1.21.0','version_1_21_0'"
  })
  void camelCase(String text, String result) {
    assertEquals(result, CamelCase.camelCase(text));
  }
}
