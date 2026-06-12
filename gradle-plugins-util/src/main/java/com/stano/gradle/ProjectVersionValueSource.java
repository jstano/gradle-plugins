package com.stano.gradle;

import org.gradle.api.provider.ValueSource;
import org.gradle.api.provider.ValueSourceParameters;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectVersionValueSource
    implements ValueSource<String, ValueSourceParameters.None> {
  //   @Inject
  //   abstract ExecOperations getExecOperations();
  @Nullable
  @Override
  public String obtain() {
    //      ByteArrayOutputStream output = new ByteArrayOutputStream();
    //      getExecOperations().exec((it) -> {
    //         it.commandLine("git", "--version");
    //         it.setStandardOutput(output);
    //      });
    return "";
  }
}
