package com.stano.gradle;

import org.gradle.api.JavaVersion;

public class JavaVersionProvider {

   public JavaVersion currentVersion() {

      return JavaVersion.current();
   }
}
