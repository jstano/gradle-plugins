package com.stano.gradle.kotlin.features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.testfixtures.ProjectBuilder;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;
import org.junit.jupiter.api.Test;

class ConfigureCompilersFeatureTest {
  @Test
  void applyingShouldConfigureKotlinCompileTasks() {
    var project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("org.jetbrains.kotlin.jvm");

    var feature = new ConfigureCompilersFeature();
    feature.apply(project);

    var compileKotlinTasks =
        project.getTasks().withType(KotlinCompile.class).getByName("compileKotlin");

    var freeCompilerArgs = compileKotlinTasks.getCompilerOptions().getFreeCompilerArgs().get();
    assertTrue(freeCompilerArgs.contains("-Xlint:none"), "Should contain -Xlint:none compiler arg");
    assertTrue(
        freeCompilerArgs.contains("-Xdoclint:none"), "Should contain -Xdoclint:none compiler arg");
    assertTrue(freeCompilerArgs.contains("-nowarn"), "Should contain -nowarn compiler arg");
    assertTrue(freeCompilerArgs.contains("-parameters"), "Should contain -parameters compiler arg");
    assertTrue(compileKotlinTasks.getIncremental(), "Should have incremental compilation enabled");
  }
}
