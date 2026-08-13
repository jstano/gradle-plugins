package com.stano.gradle.npm;

import com.stano.gradle.base.GradlePluginUtil;
import org.gradle.api.Project;

public class NpmPluginUtils {
  public static boolean shouldExecute(Project project) {
    if (GradlePluginUtil.isRunningInsideIde()) {
      NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
      return npmExtension.getRunNpmBuildInIde().get();
    }

    return true;
  }

  private NpmPluginUtils() {}
}
