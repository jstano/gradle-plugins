package com.stano.gradle.changecase;

public class UpperCaseFirst {
  public static String upperCaseFirst(String text) {
    if (StringUtils.isBlank(text)) {
      return text;
    }
    return Character.toUpperCase(text.charAt(0)) + text.substring(1);
  }
}
