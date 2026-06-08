package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SnakeCaseTest {
    @ParameterizedTest
    @CsvSource({
        ",",
        "'',''",
        "'_id','id'",
        "test,test",
        "'test string','test_string'",
        "'Test String','test_string'",
        "'TestV2','test_v2'",
        "'version 1.2.10','version_1_2_10'",
        "'version 1.21.0','version_1_21_0'"
    })
    void snakeCase(String text, String result) {
        assertEquals(result, SnakeCase.snakeCase(text));
    }
}
