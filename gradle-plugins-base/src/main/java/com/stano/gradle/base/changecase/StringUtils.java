package com.stano.gradle.base.changecase;

public final class StringUtils {
  public static boolean isBlank(String text) {
    return text == null || text.trim().isEmpty();
  }
}
