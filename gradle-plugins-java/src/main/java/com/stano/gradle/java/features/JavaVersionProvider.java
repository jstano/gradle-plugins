package com.stano.gradle.java.features;

import org.gradle.api.JavaVersion;

public class JavaVersionProvider {
  public JavaVersion currentVersion() {
    return JavaVersion.current();
  }
}
