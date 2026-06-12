package com.stano.gradle.changecase;

public class LowerCaseTransform implements Transform {
  @Override
  public String transform(String part, int index, String[] parts) {
    return part.toLowerCase();
  }
}
