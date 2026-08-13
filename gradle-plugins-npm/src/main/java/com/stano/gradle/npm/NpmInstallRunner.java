package com.stano.gradle.npm;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.gradle.process.ExecOperations;

public class NpmInstallRunner {
  private static final Set<File> installedFolders = ConcurrentHashMap.newKeySet();

  public static void npmInstall(
      ExecOperations execOperations, File packageDir, boolean useNvm, String nodeVersion) {
    if (!installedFolders.add(packageDir)) {
      return;
    }

    new NpmExecRunner(
            execOperations,
            packageDir,
            useNvm,
            nodeVersion,
            BaseNpmTask.getDefaultNodeEnvironment(),
            List.of("install"))
        .run();
  }

  private NpmInstallRunner() {}
}
