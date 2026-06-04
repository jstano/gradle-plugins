package com.stano.gradle.changecase;

public final class StringUtils {

   public static boolean isBlank(String text) {

      return text == null || text.trim().isEmpty();
   }
}
