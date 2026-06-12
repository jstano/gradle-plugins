package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class DotCase {
  /**
   * Transform into a lower case string with a period between words. "test string" becomes
   * "test.string"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String dotCase(String text) {
    return noCase(text, Options.options().withDelimiter("."));
  }
}
