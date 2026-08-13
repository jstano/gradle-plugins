package com.stano.gradle.npm;

import com.stano.gradle.base.GradlePluginUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;

class NpmExecRunner {
  private static final Logger log = Logging.getLogger(NpmExecRunner.class);

  private final ExecOperations execOperations;
  private final File projectDir;
  private final boolean useNvm;
  private final String nodeVersion;
  private final Map<String, String> environment;
  private final List<String> arguments;

  NpmExecRunner(
      ExecOperations execOperations,
      File projectDir,
      boolean useNvm,
      String nodeVersion,
      Map<String, String> environment,
      List<String> arguments) {
    this.execOperations = execOperations;
    this.projectDir = projectDir;
    this.useNvm = useNvm;
    this.nodeVersion = nodeVersion;
    this.environment = environment;
    this.arguments = arguments;
  }

  void run() {
    List<String> commandLine = new ArrayList<>(getExecutable());
    commandLine.addAll(arguments);

    ExecResult result =
        execOperations.exec(
            spec -> {
              spec.setCommandLine(commandLine);
              spec.setWorkingDir(projectDir);
              spec.environment(environment);
              spec.setIgnoreExitValue(true);
            });

    if (result.getExitValue() != 0) {
      throw new GradleException(
          String.format("npm failed with exit code %d", result.getExitValue()));
    }
  }

  private List<String> getExecutable() {
    if (useNvm()) {
      List<String> npmExecCommand = getNvmExecCommand(nodeVersion);
      log.info("Running with nvm {}", String.join(" ", npmExecCommand));
      return npmExecCommand;
    }

    if (GradlePluginUtil.isWindows()) {
      return List.of("npm.cmd");
    }

    return List.of("npm");
  }

  private boolean useNvm() {
    if (useNvm) {
      log.info("Checking if nvm is available");

      if (System.getenv("NVM_HOME") != null) {
        log.info("NVM_HOME is set, building using nvm");
        return true;
      }

      if (new File(System.getProperty("user.home"), ".nvm/nvm.sh").exists()) {
        log.info("Found .nvm/nvm.sh, building using nvm");
        return true;
      }

      log.info("Unable to locate nvm, building without nvm");
    }

    return false;
  }

  private List<String> getNvmExecCommand(String nodeVersion) {
    if (GradlePluginUtil.isWindows()) {
      return List.of(NvmWindowsUtil.getNpmExe(nodeVersion));
    }

    try {
      File nvmFile = File.createTempFile("--nvmsh--", null);
      nvmFile.deleteOnExit();

      try (InputStream in = getClass().getClassLoader().getResourceAsStream("nvm.sh")) {
        Files.copy(in, nvmFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }

      nvmFile.setExecutable(true);

      return List.of(nvmFile.getAbsolutePath(), "exec", nodeVersion, "npm");
    } catch (IOException x) {
      throw new GradleException("Error creating nvm.sh", x);
    }
  }
}
