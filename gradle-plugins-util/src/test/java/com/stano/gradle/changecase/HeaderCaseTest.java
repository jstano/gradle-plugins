package com.stano.gradle.changecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderCaseTest {
    @ParameterizedTest
    @CsvSource({
        ",",
        "'',''",
        "test,Test",
        "'test string','Test-String'",
        "'Test String','Test-String'",
        "'TestV2','Test-V2'",
        "'version 1.2.10','Version-1-2-10'",
        "'version 1.21.0','Version-1-21-0'"
    })
    void headerCase(String text, String result) {
        assertEquals(result, HeaderCase.headerCase(text));
    }
}
