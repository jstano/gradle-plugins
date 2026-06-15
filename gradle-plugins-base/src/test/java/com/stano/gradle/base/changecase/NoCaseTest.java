package com.stano.gradle.base.changecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NoCaseTest {
  @ParameterizedTest
  @CsvSource({
    "test,test",
    "TEST,test",
    "'testString','test string'",
    "'testString123','test string123'",
    "'testString_1_2_3','test string 1 2 3'",
    "'x_256','x 256'",
    "'anHTMLTag','an html tag'",
    "'ID123String','id123 string'",
    "'Id123String','id123 string'",
    "'foo bar123','foo bar123'",
    "'a1bStar','a1b star'",
    "'CONSTANT_CASE ','constant case'",
    "'CONST123_FOO','const123 foo'",
    "'FOO_bar','foo bar'",
    "'XMLHttpRequest','xml http request'",
    "'IQueryAArgs','i query a args'",
    "'dot.case','dot case'",
    "'path/case','path case'",
    "'snake_case','snake case'",
    "'snake_case123','snake case123'",
    "'snake_case_123','snake case 123'",
    "'\"quotes\"','quotes'",
    "'version 0.45.0','version 0 45 0'",
    "'version 0..78..9','version 0 78 9'",
    "'version 4_99/4','version 4 99 4'",
    "'  test  ','test'",
    "'something_2014_other','something 2014 other'",
    "'amazon s3 data','amazon s3 data'",
    "'foo_13_bar','foo 13 bar'"
  })
  void noCase(String text, String result) {
    assertEquals(result, NoCase.noCase(text));
  }
}
