package com.stano.gradle;

import java.util.Map;

public class SystemEnvironment implements Environment {
  @Override
  public Map<String, String> getAllEnvironmentVariables() {
    return System.getenv();
  }

  @Override
  public String getEnvironmentVariable(String name) {
    return System.getenv(name);
  }
}
