package com.stano.gradle.changecase;

import java.util.regex.Pattern;

public class Options {
  // Support camel case ("camelCase" -> "camel Case" and "CAMELCase" -> "CAMEL Case").
  private static final Pattern[] DEFAULT_SPLIT_REGEXP = {
    Pattern.compile("([a-z0-9])([A-Z])"), Pattern.compile("([A-Z])([A-Z][a-z])")
  }; // [/([a-z0-9])([A-Z])/g, /([A-Z])([A-Z][a-z])/g]
  // Remove all non-word characters.
  private static final Pattern[] DEFAULT_STRIP_REGEXP = {
    Pattern.compile("[^A-Z0-9]+", Pattern.CASE_INSENSITIVE)
  };
  private static final String DEFAULT_DELIMITER = " ";
  private static final Transform DEFAULT_TRANSFORM = new LowerCaseTransform();
  private final Pattern[] splitRegexp;
  private final Pattern[] stripRegexp;
  private final String delimiter;
  private final Transform transform;

  public static Options options() {
    return new Options(
        DEFAULT_SPLIT_REGEXP, DEFAULT_STRIP_REGEXP, DEFAULT_DELIMITER, DEFAULT_TRANSFORM);
  }

  public Options withDelimiter(String delimiter) {
    return new Options(this.splitRegexp, this.stripRegexp, delimiter, this.transform);
  }

  public Options withTransform(Transform transform) {
    return new Options(this.splitRegexp, this.stripRegexp, this.delimiter, transform);
  }

  public Pattern[] getSplitRegexp() {
    return splitRegexp;
  }

  public Pattern[] getStripRegexp() {
    return stripRegexp;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public Transform getTransform() {
    return transform;
  }

  private Options(
      Pattern[] splitRegexp, Pattern[] stripRegexp, String delimiter, Transform transform) {
    this.splitRegexp = splitRegexp;
    this.stripRegexp = stripRegexp;
    this.delimiter = delimiter;
    this.transform = transform;
  }
}
