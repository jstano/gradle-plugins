package com.stano.gradle.javacommon;

import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

public class CompilerUtils {
  public void configureJavaCompiler(Project project) {
    project
        .getTasks()
        .withType(
            JavaCompile.class,
            javaCompile -> {
              javaCompile.getOptions().setIncremental(true);
              javaCompile.getOptions().setFork(true);
              javaCompile
                  .getOptions()
                  .getForkOptions()
                  .setJvmArgs(Arrays.asList("-Xmx4096m", "-Dhttp.agent=wtf"));
              javaCompile.getOptions().setCompilerArgs(getCompileOptions());
            });
  }

  public void configureKotlinCompiler(Project project) {
    project
        .getTasks()
        .withType(
            KotlinCompile.class,
            kotlinCompile -> {
              kotlinCompile.getCompilerOptions().getFreeCompilerArgs().addAll(getCompileOptions());
              kotlinCompile.setIncremental(true);
            });
  }

  private List<String> getCompileOptions() {
    return Arrays.asList("-Xlint:none", "-Xdoclint:none", "-nowarn", "-parameters");
  }
}
