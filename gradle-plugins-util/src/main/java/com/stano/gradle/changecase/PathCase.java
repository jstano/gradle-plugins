package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class PathCase {
  /**
   * Transform into a lower case string with slashes between words. "test string" becomes
   * "test/string"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String pathCase(String text) {
    return noCase(text, Options.options().withDelimiter("/"));
  }
}
