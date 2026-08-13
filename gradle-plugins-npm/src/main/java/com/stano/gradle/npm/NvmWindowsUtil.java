package com.stano.gradle.npm;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import org.gradle.api.GradleException;

class NvmWindowsUtil {
  static String getNpmExe(String nodeVersion) {
    installNodeVersion(nodeVersion);

    return getNpmPath(nodeVersion);
  }

  private static void installNodeVersion(String nodeVersion) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("nvm", "install", nodeVersion);
      Process process = processBuilder.start();
      int result = process.waitFor();

      if (result != 0) {
        throw new GradleException(String.format("nvm install failed with exit code %d", result));
      }
    } catch (Exception x) {
      throw new GradleException("nvm install failed", x);
    }
  }

  private static String getNpmPath(String nodeVersion) {
    String nvmHome = System.getenv("NVM_HOME");
    File[] files = new File(nvmHome).listFiles();

    if (files != null) {
      Optional<String> npmFolder =
          Arrays.stream(files)
              .filter(File::isDirectory)
              .filter(it -> it.getName().startsWith("v" + nodeVersion))
              .map(File::getAbsolutePath)
              .findFirst();

      if (npmFolder.isPresent()) {
        return npmFolder.get() + File.separator + "npm.cmd";
      }
    }

    throw new GradleException(
        "Unable to locate an nvm managed node installation for version " + nodeVersion);
  }

  private NvmWindowsUtil() {}
}
