package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentenceCaseTest {
  @ParameterizedTest
  @CsvSource({
    ",",
    "'',''",
    "test,Test",
    "'test string','Test string'",
    "'Test String','Test string'",
    "'TestV2','Test v2'",
    "'version 1.2.10','Version 1 2 10'",
    "'version 1.21.0','Version 1 21 0'"
  })
  void sentenceCase(String text, String result) {
    assertEquals(result, SentenceCase.sentenceCase(text));
  }
}
