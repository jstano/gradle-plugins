package com.stano.gradle.base;

import java.io.Serializable;
import java.util.Map;

public class SystemEnvironment implements Environment, Serializable {
  @Override
  public Map<String, String> getAllEnvironmentVariables() {
    return System.getenv();
  }

  @Override
  public String getEnvironmentVariable(String name) {
    return System.getenv(name);
  }
}
