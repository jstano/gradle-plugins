package com.stano.gradle.java;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.stano.gradle.base.BasePluginTest;
import org.junit.jupiter.api.Test;

class ConfigurePluginsFeatureTest extends BasePluginTest {
  @Test
  void applyingJavaPluginShouldNotApplyKotlinJvmPlugin() {
    rootProject.getPluginManager().apply("com.stano.base");
    childProject.getPluginManager().apply("com.stano.java");
    assertFalse(
        childProject.getPluginManager().hasPlugin("org.jetbrains.kotlin.jvm"),
        "com.stano.java should not apply kotlin.jvm; use com.stano.kotlin for Kotlin support");
  }
}
