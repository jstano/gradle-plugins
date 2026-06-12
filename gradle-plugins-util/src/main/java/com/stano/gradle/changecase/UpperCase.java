package com.stano.gradle.changecase;

public class UpperCase {
  public static String upperCase(String text) {
    if (StringUtils.isBlank(text)) {
      return text;
    }
    return text.toUpperCase();
  }

  public static final Transform upperCaseTransform = new UpperCaseTransform();

  private static class UpperCaseTransform implements Transform {
    @Override
    public String transform(String part, int index, String[] parts) {
      return part.toUpperCase();
    }
  }
}
