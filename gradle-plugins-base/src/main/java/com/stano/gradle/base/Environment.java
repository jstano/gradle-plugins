package com.stano.gradle.base;

import java.util.Map;

public interface Environment {
  Map<String, String> getAllEnvironmentVariables();

  String getEnvironmentVariable(String name);
}
