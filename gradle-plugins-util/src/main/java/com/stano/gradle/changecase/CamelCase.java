package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class CamelCase {
  /**
   * Transform into a string with the separator denoted by the next word capitalized. "test string"
   * becomes "testString"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String camelCase(String text) {
    return noCase(text, Options.options().withDelimiter("").withTransform(camelCaseTransform));
  }

  public static final Transform camelCaseTransform = new CamelCaseTransform();
  public static final Transform camelCaseTransformMerge = new CamelCaseTransformMerge();

  private static class CamelCaseTransform implements Transform {
    @Override
    public String transform(String part, int index, String[] parts) {
      if (index == 0) {
        return part.toLowerCase();
      }
      return PascalCase.pascalCaseTransform.transform(part, index, parts);
    }
  }

  private static class CamelCaseTransformMerge implements Transform {
    @Override
    public String transform(String part, int index, String[] parts) {
      if (index == 0) {
        return part.toLowerCase();
      }
      return PascalCase.pascalCaseTransformMerge.transform(part, index, parts);
    }
  }
}
