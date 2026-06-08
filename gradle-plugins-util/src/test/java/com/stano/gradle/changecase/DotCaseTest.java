package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DotCaseTest {
    @ParameterizedTest
    @CsvSource({
        ",",
        "'',''",
        "test,test",
        "'test string','test.string'",
        "'Test String','test.string'",
        "'dot.case','dot.case'",
        "'path/case','path.case'",
        "'TestV2','test.v2'",
        "'version 1.2.10','version.1.2.10'",
        "'version 1.21.0','version.1.21.0'"
    })
    void dotCase(String text, String result) {
        assertEquals(result, DotCase.dotCase(text));
    }
}
