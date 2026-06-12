package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class ParamCase {
  /**
   * Transform into a lower cased string with dashes between words. "test string" becomes
   * "test-string"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String paramCase(String text) {
    return noCase(text, Options.options().withDelimiter("-"));
  }
}
