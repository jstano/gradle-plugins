package com.stano.gradle.docker;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.util.TeeOutputStream;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

final class GradleExecUtils {
  public static void execWithErrorMessage(
      Project project, ExecOperations execOperations, Action<ExecSpec> execSpecAction) {
    List<String> commandLine = new ArrayList<>();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExecResult execResult =
        execOperations.exec(
            execSpec -> {
              execSpecAction.execute(execSpec);
              execSpec.setIgnoreExitValue(true);
              execSpec.setStandardOutput(new TeeOutputStream(System.out, output));
              execSpec.setErrorOutput(new TeeOutputStream(System.err, output));
              commandLine.addAll(execSpec.getCommandLine());
            });
    if (execResult.getExitValue() != 0) {
      throw new GradleException(
          String.format(
              "The command '%s' failed with exit code %d. Output:\n%s",
              commandLine,
              execResult.getExitValue(),
              new String(output.toByteArray(), StandardCharsets.UTF_8)));
    }
  }

  private GradleExecUtils() {}
}
