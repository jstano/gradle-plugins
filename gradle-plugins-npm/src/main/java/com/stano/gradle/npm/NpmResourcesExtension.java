package com.stano.gradle.npm;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.GradlePluginUtil;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class NpmResourcesExtension {
  public static final String NPM_RESOURCE_OUTPUT_PATH = "npmResourceOutputPath";

  private final Property<String> assembleOutputPath;
  private final Property<String> resourceOutputPath;

  @Inject
  public NpmResourcesExtension(Project project) {
    ObjectFactory objectFactory = project.getObjects();

    assembleOutputPath =
        objectFactory.property(String.class).convention(getDefaultAssembleOutputPath(project));
    resourceOutputPath =
        objectFactory
            .property(String.class)
            .convention(
                GradlePluginUtil.getProjectOrSystemProperty(project, NPM_RESOURCE_OUTPUT_PATH, ""));
  }

  public Property<String> getAssembleOutputPath() {
    return assembleOutputPath;
  }

  public Property<String> getResourceOutputPath() {
    return resourceOutputPath;
  }

  private String getDefaultAssembleOutputPath(Project project) {
    String npmOutputPath = GradlePluginUtil.getProjectProperty(project, "npmOutputPath");
    if (npmOutputPath != null) {
      return npmOutputPath;
    }

    String contextName = GradlePluginUtil.getProjectProperty(project, "contextName");
    if (contextName != null) {
      return contextName;
    }

    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    return baseExtension.getContextName();
  }
}
