package com.stano.gradle.java.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.LockMode;

public class ConfigureDependencyLockingFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    if (!Boolean.TRUE.equals(baseExtension.getDependencyLocking())) {
      return;
    }

    project.getDependencyLocking().lockAllConfigurations();
    project.getDependencyLocking().getLockMode().set(LockMode.STRICT);
  }
}
