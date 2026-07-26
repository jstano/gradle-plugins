package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Zip;

public class ConfigureMavenCentralStagingZipFeature implements PluginFeature {
  public static final String TASK_NAME = "zipStagingDeploy";

  @Override
  public void apply(Project project) {
    project
        .getTasks()
        .register(
            TASK_NAME,
            Zip.class,
            zip -> {
              zip.setGroup("publishing");
              zip.getArchiveFileName().set("staging-deploy.zip");
              zip.getDestinationDirectory().set(project.getLayout().getBuildDirectory().dir("tmp"));
              zip.from(
                  project.getLayout().getBuildDirectory().dir("staging-deploy"),
                  copySpec -> copySpec.include("**/*"));
            });
  }
}
