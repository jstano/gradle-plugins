package com.stano.gradle.npm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.OutputDirectory;

@CacheableTask
public abstract class NpmAssembleTask extends Copy {
  private final File buildDir;
  private final String projectName;
  private final NpmResourcesExtension npmResourcesExtension;
  private final File projectDir;

  @Inject
  public NpmAssembleTask(Project project) {
    setGroup("build");
    setDescription("Copy the output from the npm build folder to the resources/main/public folder");

    NpmExtension npmExtension = project.getExtensions().getByType(NpmExtension.class);
    npmResourcesExtension = project.getExtensions().getByType(NpmResourcesExtension.class);
    projectDir = npmExtension.getProjectDirectory().get();
    buildDir = project.getLayout().getBuildDirectory().getAsFile().get();
    projectName = getProjectName(project);

    from(project.file(new File(npmExtension.getProjectDistRoot().get(), "dist")));
    include("**/*");
  }

  @OutputDirectory
  @Override
  public File getDestinationDir() {
    String npmOutputPath = npmResourcesExtension.getResourceOutputPath().get();
    String destinationPath =
        npmOutputPath.isEmpty()
            ? "resources/main/public/"
                + npmResourcesExtension.getAssembleOutputPath().get()
                + "/"
                + projectName
            : npmOutputPath;
    return new File(buildDir, destinationPath);
  }

  private String getProjectName(Project project) {
    File packageJsonFile = project.file(new File(projectDir, "package.json"));

    if (packageJsonFile.exists()) {
      try {
        JsonNode rootNode = new ObjectMapper().readTree(packageJsonFile);
        JsonNode nameNode = rootNode.get("name");

        if (nameNode != null) {
          return nameNode.asText();
        }
      } catch (IOException x) {
        throw new GradleException(
            "Unable to read the name from the package.json file: "
                + packageJsonFile.getAbsolutePath(),
            x);
      }
    }

    throw new GradleException(
        "Unable to read the name from the package.json file: " + packageJsonFile.getAbsolutePath());
  }
}
