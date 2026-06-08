package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PascalCaseTest {
    @ParameterizedTest
    @CsvSource({
        ",",
        "'',''",
        "test,Test",
        "'test string','TestString'",
        "'Test String','TestString'",
        "'TestV2','TestV2'",
        "'version 1.2.10','Version_1_2_10'",
        "'version 1.21.0','Version_1_21_0'"
    })
    void pascalCase(String text, String result) {
        assertEquals(result, PascalCase.pascalCase(text));
    }
}
