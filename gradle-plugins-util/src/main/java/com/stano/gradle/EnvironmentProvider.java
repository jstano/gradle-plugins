package com.stano.gradle;

public final class EnvironmentProvider {
  private static Environment environment = new SystemEnvironment();

  public static Environment getEnvironment() {
    return environment;
  }

  public static void setEnvironment(Environment environment) {
    EnvironmentProvider.environment = environment;
  }

  public static void reset() {
    EnvironmentProvider.environment = new SystemEnvironment();
  }

  private EnvironmentProvider() {}
}
