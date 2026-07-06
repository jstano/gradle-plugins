package com.stano.gradle.springboot.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BuildInfoProvider;
import com.stano.gradle.base.BuildInfoUpdater;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.base.SystemEnvironment;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

public class ConfigureBuildInfoFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    String contextName = baseExtension.getContextName();
    Object version = project.getVersion();
    Provider<RegularFile> applicationYmlFile =
        project.getLayout().getBuildDirectory().file("resources/main/application.yml");
    project
        .getTasks()
        .named(
            "processResources",
            t ->
                t.doLast(
                    task -> {
                      String applicationYmlPath =
                          applicationYmlFile.get().getAsFile().getAbsolutePath();
                      BuildInfoUpdater.updateYmlWithBuildInfo(
                          contextName,
                          version.toString(),
                          new BuildInfoProvider(new SystemEnvironment()),
                          applicationYmlPath);
                    }));
  }
}
