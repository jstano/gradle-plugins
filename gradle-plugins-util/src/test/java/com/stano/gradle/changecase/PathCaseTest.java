package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathCaseTest {
    @ParameterizedTest
    @CsvSource({
        ",",
        "'',''",
        "test,test",
        "'test string','test/string'",
        "'Test String','test/string'",
        "'TestV2','test/v2'",
        "'version 1.2.10','version/1/2/10'",
        "'version 1.21.0','version/1/21/0'"
    })
    void pathCase(String text, String result) {
        assertEquals(result, PathCase.pathCase(text));
    }
}
