package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantCaseTest {
  @ParameterizedTest
  @CsvSource({
    ",",
    "'',''",
    "test,TEST",
    "'test string','TEST_STRING'",
    "'Test String','TEST_STRING'",
    "'dot.case','DOT_CASE'",
    "'path/case','PATH_CASE'",
    "'TestV2','TEST_V2'",
    "'version 1.2.10','VERSION_1_2_10'",
    "'version 1.21.0','VERSION_1_21_0'",
    "'stano.sonar.host.url','STANO_SONAR_HOST_URL'",
    "'stano.sonar.token','STANO_SONAR_TOKEN'"
  })
  void constantCase(String text, String result) {
    assertEquals(result, ConstantCase.constantCase(text));
  }
}
