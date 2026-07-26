package com.stano.gradle.docker;

import java.io.File;
import java.util.List;

final class DockerExecutable {
  private static final List<String> COMMON_LOCATIONS =
      List.of("/opt/homebrew/bin/docker", "/usr/local/bin/docker", "/usr/bin/docker");

  private DockerExecutable() {}

  static String resolve() {
    String pathEnv = System.getenv("PATH");
    if (pathEnv != null) {
      for (String dir : pathEnv.split(File.pathSeparator)) {
        File candidate = new File(dir, "docker");
        if (candidate.isFile() && candidate.canExecute()) {
          return candidate.getAbsolutePath();
        }
      }
    }
    for (String candidate : COMMON_LOCATIONS) {
      if (new File(candidate).canExecute()) {
        return candidate;
      }
    }
    return "docker";
  }
}
