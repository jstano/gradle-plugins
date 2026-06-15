package com.stano.gradle.springboot.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BuildInfoProvider;
import com.stano.gradle.base.BuildInfoUpdater;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.base.SystemEnvironment;
import org.gradle.api.Project;

public class ConfigureBuildInfoFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    project
        .getTasks()
        .named(
            "processResources",
            t ->
                t.doLast(
                    task -> {
                      String applicationYmlPath =
                          project
                                  .getLayout()
                                  .getBuildDirectory()
                                  .get()
                                  .getAsFile()
                                  .getAbsolutePath()
                              + "/resources/main/application.yml";
                      BuildInfoUpdater.updateYmlWithBuildInfo(
                          baseExtension.getContextName(),
                          project.getVersion().toString(),
                          new BuildInfoProvider(new SystemEnvironment()),
                          applicationYmlPath);
                    }));
  }
}
