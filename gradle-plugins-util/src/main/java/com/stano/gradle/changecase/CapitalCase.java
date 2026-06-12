package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;
import static com.stano.gradle.changecase.UpperCaseFirst.upperCaseFirst;

public class CapitalCase {
  /**
   * Transform into a space separated string with each word capitalized. "test string" becomes "Test
   * String"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String capitalCase(String text) {
    return capitalCase(text, Options.options());
  }

  public static String capitalCase(String text, Options options) {
    return noCase(text, options.withTransform(capitalCaseTransform));
  }

  public static final Transform capitalCaseTransform = new CapitalCaseTransform();

  private static class CapitalCaseTransform implements Transform {
    @Override
    public String transform(String part, int index, String[] parts) {
      return upperCaseFirst(part);
    }
  }
}
