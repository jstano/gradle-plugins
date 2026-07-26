package com.stano.gradle.java.features;

import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;

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

  private List<String> getCompileOptions() {
    return Arrays.asList("-Xlint:none", "-Xdoclint:none", "-nowarn", "-parameters");
  }
}
