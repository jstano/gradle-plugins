package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class SentenceCase {
  /**
   * Transform into a lower case with spaces between words, then capitalize the string. "test
   * string" becomes "Test string"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String sentenceCase(String text) {
    return noCase(text, Options.options().withDelimiter(" ").withTransform(sentenceCaseTransform));
  }

  public static final Transform sentenceCaseTransform = new SentenceCaseTransform();

  private static class SentenceCaseTransform implements Transform {
    @Override
    public String transform(String part, int index, String[] parts) {
      String result = part.toLowerCase();
      if (index == 0) {
        return UpperCaseFirst.upperCaseFirst(result);
      }
      return result;
    }
  }
}
