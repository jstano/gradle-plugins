package com.stano.gradle.javaconventions;

import org.gradle.api.JavaVersion;

public class JavaVersionProvider {
  public JavaVersion currentVersion() {
    return JavaVersion.current();
  }
}
