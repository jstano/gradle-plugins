package com.stano.gradle.kotlin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import org.junit.jupiter.api.Test;

class KotlinPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldApplyTheKotlinJvmPlugin() {
    rootProject.getPluginManager().apply("com.stano.base");
    childProject.getPluginManager().apply("com.stano.kotlin");
    assertTrue(childProject.getPluginManager().hasPlugin("org.jetbrains.kotlin.jvm"));
  }

  @Test
  void applyingThePluginShouldTransparentlyApplyComStanoJava() {
    rootProject.getPluginManager().apply("com.stano.base");
    childProject.getPluginManager().apply("com.stano.kotlin");
    assertTrue(childProject.getPluginManager().hasPlugin("com.stano.java"));
  }
}
