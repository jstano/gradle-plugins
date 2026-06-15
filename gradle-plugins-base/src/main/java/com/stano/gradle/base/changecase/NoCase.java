package com.stano.gradle.base.changecase;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NoCase {
  /**
   * Transform into a lower cased string with spaces between words. "testString" becomes "test
   * string"
   *
   * @param text The text to transform
   * @return The transformed text
   */
  public static String noCase(String text) {
    return noCase(text, Options.options());
  }

  /**
   * Transform into a lower cased string with spaces between words. "testString" becomes "test
   * string"
   *
   * @param text The text to transform
   * @param options The transformation options
   * @return The transformed text
   */
  public static String noCase(String text, Options options) {
    if (StringUtils.isBlank(text)) {
      return text;
    }
    text = replace(text, options.getSplitRegexp(), "$1\0$2");
    text = replace(text, options.getStripRegexp(), "\0");
    // trim the delimiter from around the output string
    int start = 0;
    int end = text.length();
    if (text.length() > 0) {
      while (text.charAt(start) == '\0') {
        start++;
      }
      while (text.charAt(end - 1) == '\0') {
        end--;
      }
      text = text.substring(start, end);
    }
    // transform each token independently
    String[] parts = text.split("\0");
    String[] newParts = new String[parts.length];
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      newParts[i] = options.getTransform().transform(part, i, parts);
    }
    return Arrays.stream(newParts).collect(Collectors.joining(options.getDelimiter()));
  }

  private static String replace(String text, Pattern[] patterns, String value) {
    for (Pattern pattern : patterns) {
      text = pattern.matcher(text).replaceAll(value);
    }
    return text;
  }
}
