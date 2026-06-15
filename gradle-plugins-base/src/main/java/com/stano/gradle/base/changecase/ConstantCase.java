package com.stano.gradle.base.changecase;

import static com.stano.gradle.base.changecase.NoCase.noCase;
import static com.stano.gradle.base.changecase.UpperCase.upperCaseTransform;

public class ConstantCase {
  /**
   * Transform into upper case string with an underscore between words. "test string" becomes
   * "TEST_STRING"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String constantCase(String text) {
    return noCase(text, Options.options().withDelimiter("_").withTransform(upperCaseTransform));
  }
}
