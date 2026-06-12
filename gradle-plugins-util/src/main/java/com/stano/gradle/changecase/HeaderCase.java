package com.stano.gradle.changecase;

public class HeaderCase {
  /**
   * Transform into a dash separated string of capitalized words. "test string" becomes
   * "Test-String"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String headerCase(String text) {
    return CapitalCase.capitalCase(text, Options.options().withDelimiter("-"));
  }
}
