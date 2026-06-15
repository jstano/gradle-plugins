package com.stano.gradle.base.changecase;

public interface Transform {
  String transform(String part, int index, String[] parts);
}
