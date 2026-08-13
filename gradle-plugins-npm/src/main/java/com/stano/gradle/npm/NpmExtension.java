package com.stano.gradle.npm;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.GradlePluginUtil;
import java.io.File;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class NpmExtension {
  public static final String NPM_PROJECT_PATH = "npmProjectPath";

  private final Property<String> resourceFilesDirectory;
  private final Property<Boolean> runNpmBuildInIde;
  private final Property<Boolean> useNvm;
  private final Property<String> nodeVersion;
  private final Property<File> projectDirectory;
  private final Property<File> projectDistRoot;

  @Inject
  public NpmExtension(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    ObjectFactory objectFactory = project.getObjects();

    resourceFilesDirectory = objectFactory.property(String.class);
    runNpmBuildInIde =
        objectFactory
            .property(Boolean.class)
            .convention(
                Boolean.parseBoolean(
                    GradlePluginUtil.getProjectOrSystemProperty(project, "runNpmBuildInIde")));
    useNvm = objectFactory.property(Boolean.class).convention(baseExtension.isUseNvm());
    nodeVersion =
        objectFactory.property(String.class).convention(baseExtension.getDefaultNodeVersion());
    projectDirectory =
        objectFactory.property(File.class).convention(getDefaultProjectDirectory(project));
    projectDistRoot =
        objectFactory.property(File.class).convention(getDefaultProjectBuildDirectory(project));
  }

  public Property<String> getResourceFilesDirectory() {
    return resourceFilesDirectory;
  }

  public Property<Boolean> getRunNpmBuildInIde() {
    return runNpmBuildInIde;
  }

  public Property<Boolean> getUseNvm() {
    return useNvm;
  }

  public Property<String> getNodeVersion() {
    return nodeVersion;
  }

  public Property<File> getProjectDirectory() {
    return projectDirectory;
  }

  public Property<File> getProjectDistRoot() {
    return projectDistRoot;
  }

  private File getDefaultProjectDirectory(Project project) {
    // reading from project property as we need projectDir in the configuration phase
    String npmProjectPath = GradlePluginUtil.getProjectOrSystemProperty(project, NPM_PROJECT_PATH);
    Directory gradleProjectDir = project.getLayout().getProjectDirectory();
    return npmProjectPath == null || npmProjectPath.isEmpty()
        ? gradleProjectDir.getAsFile()
        : gradleProjectDir.dir(npmProjectPath).getAsFile();
  }

  private File getDefaultProjectBuildDirectory(Project project) {
    // reading from project property as we need buildDir in the configuration phase
    String npmProjectPath = GradlePluginUtil.getProjectOrSystemProperty(project, NPM_PROJECT_PATH);
    return npmProjectPath == null || npmProjectPath.isEmpty()
        ? project.getLayout().getBuildDirectory().get().getAsFile()
        : project.getLayout().getProjectDirectory().dir(npmProjectPath).getAsFile();
  }
}
