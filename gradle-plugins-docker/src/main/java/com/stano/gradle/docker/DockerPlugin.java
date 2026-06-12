package com.stano.gradle.docker;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.bundling.Zip;

public class DockerPlugin implements Plugin<Project> {
  private static final Logger log = Logging.getLogger(DockerPlugin.class);
  private static final Pattern LABEL_KEY_PATTERN = Pattern.compile("^[a-z0-9.-]*$");
  private final ObjectFactory objectFactory;

  @Inject
  public DockerPlugin(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public void apply(Project project) {
    DockerExtension ext = project.getExtensions().create("docker", DockerExtension.class, project);
    if (project.getConfigurations().findByName("docker") == null) {
      project.getConfigurations().create("docker");
    }
    Delete clean =
        project
            .getTasks()
            .create(
                "dockerClean",
                Delete.class,
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Cleans Docker build directory.");
                });
    Copy prepare =
        project
            .getTasks()
            .create(
                "dockerPrepare",
                Copy.class,
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Prepares Docker build directory.");
                  task.dependsOn(clean);
                });
    if (project.getTasks().findByName("copyWar") != null) {
      prepare.dependsOn(project.getTasks().findByName("copyWar"));
    }
    Exec exec =
        project
            .getTasks()
            .create(
                "docker",
                Exec.class,
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Builds Docker image.");
                  task.dependsOn(prepare);
                });
    Task tag =
        project
            .getTasks()
            .create(
                "dockerTag",
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Applies all tags to the Docker image.");
                  task.dependsOn(exec);
                });
    Task pushAllTags =
        project
            .getTasks()
            .create(
                "dockerTagsPush",
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Pushes all tagged Docker images to configured Docker Hub.");
                });
    Task copyDockerImageUrl =
        project
            .getTasks()
            .create(
                "dockerImageUrl",
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Copies Docker image url to a file.");
                  task.doLast(
                      ignored -> {
                        String content = ext.getName();
                        String fileName =
                            project.getLayout().getBuildDirectory().get().getAsFile()
                                + "/docker-image-url.txt";
                        try {
                          Files.writeString(Path.of(fileName), content);
                        } catch (IOException e) {
                          throw new GradleException("Failed to write docker image URL file", e);
                        }
                        System.out.println("File created successfully: " + fileName);
                      });
                });
    copyDockerImageUrl.shouldRunAfter(pushAllTags);
    project
        .getTasks()
        .create(
            "dockerPush",
            task -> {
              task.setGroup("Docker");
              task.setDescription("Pushes named Docker image to configured Docker Hub.");
              task.dependsOn(pushAllTags);
            });
    Zip dockerfileZip =
        project
            .getTasks()
            .create(
                "dockerfileZip",
                Zip.class,
                task -> {
                  task.setGroup("Docker");
                  task.setDescription("Bundles the configured Dockerfile in a zip file");
                });
    PublishArtifact dockerArtifact =
        new ArchivePublishArtifact(
            DefaultTaskDependencyFactory.withNoAssociatedProject(), dockerfileZip);
    Configuration dockerConfiguration = project.getConfigurations().getByName("docker");
    dockerConfiguration.getArtifacts().add(dockerArtifact);
    project.getComponents().add(new DockerComponent(dockerArtifact));
    new ConfigureTasks().apply(project);
    project.afterEvaluate(
        p -> {
          ext.resolvePathsAndValidate();
          String dockerDir = p.getLayout().getBuildDirectory().get().getAsFile() + "/docker";
          clean.delete(dockerDir);
          prepare.from(ext.getCopySpec());
          prepare.from(
              ext.getResolvedDockerfile(),
              spec -> {
                spec.rename(fileName -> "Dockerfile");
              });
          prepare.into(dockerDir);
          exec.setWorkingDir(dockerDir);
          exec.commandLine(buildCommandLine(ext));
          exec.dependsOn(ext.getDependencies());
          exec.getLogging().captureStandardOutput(LogLevel.INFO);
          exec.getLogging().captureStandardError(LogLevel.ERROR);
          Map<String, TagConfig> tags = new LinkedHashMap<>();
          for (Map.Entry<String, String> entry : ext.getNamedTags().entrySet()) {
            String taskName = entry.getKey();
            String tagName = entry.getValue();
            tags.put(generateTagTaskName(taskName), new TagConfig(tagName, () -> tagName));
          }
          for (String unresolvedTagName : ext.getTags()) {
            String taskName = generateTagTaskName(unresolvedTagName);
            if (tags.containsKey(taskName)) {
              throw new IllegalArgumentException("Task name '" + taskName + "' is existed.");
            }
            String computedTag = unresolvedTagName;
            tags.put(
                taskName,
                new TagConfig(unresolvedTagName, () -> computeName(ext.getName(), computedTag)));
          }
          for (Map.Entry<String, TagConfig> entry : tags.entrySet()) {
            String taskName = entry.getKey();
            TagConfig tagConfig = entry.getValue();
            Exec tagSubTask =
                p.getTasks()
                    .create(
                        "dockerTag" + taskName,
                        Exec.class,
                        task -> {
                          task.setGroup("Docker");
                          task.setDescription(
                              "Tags Docker image with tag '" + tagConfig.tagName + "'");
                          task.setWorkingDir(dockerDir);
                          task.commandLine("docker", "tag", ext.getName(), tagConfig.tagTask.get());
                          task.dependsOn(exec);
                        });
            tag.dependsOn(tagSubTask);
            Exec pushSubTask =
                p.getTasks()
                    .create(
                        "dockerPush" + taskName,
                        Exec.class,
                        task -> {
                          task.setGroup("Docker");
                          task.setDescription(
                              "Pushes the Docker image with tag '"
                                  + tagConfig.tagName
                                  + "' to configured Docker Hub");
                          task.setWorkingDir(dockerDir);
                          task.commandLine("docker", "push", tagConfig.tagTask.get());
                          task.dependsOn(tagSubTask);
                        });
            pushAllTags.dependsOn(pushSubTask);
          }
          dockerfileZip.from(ext.getResolvedDockerfile());
        });
  }

  private List<String> buildCommandLine(DockerExtension ext) {
    List<String> buildCommandLine = new ArrayList<>();
    buildCommandLine.add("docker");
    if (ext.getBuildx()) {
      buildCommandLine.addAll(List.of("buildx", "build"));
      Set<String> platform = ext.getPlatform();
      if (platform.isEmpty()) {
        platform = ImmutableSet.of("linux/amd64");
      }
      buildCommandLine.addAll(List.of("--platform", String.join(",", platform)));
      buildCommandLine.add("--no-cache");
      buildCommandLine.add("--pull");
      if (ext.getLoad()) {
        buildCommandLine.add("--load");
      }
      if (ext.getPush()) {
        buildCommandLine.add("--push");
        if (ext.getLoad()) {
          throw new GradleException("cannot combine 'push' and 'load' options");
        }
      }
      if (ext.getBuilder() != null) {
        buildCommandLine.addAll(List.of("--builder", ext.getBuilder()));
      }
    } else {
      buildCommandLine.add("build");
    }
    if (ext.getNoCache()) {
      buildCommandLine.add("--no-cache");
    }
    if (ext.getNetwork() != null) {
      buildCommandLine.addAll(List.of("--network", ext.getNetwork()));
    }
    if (!ext.getBuildArgs().isEmpty()) {
      for (Map.Entry<String, String> buildArg : ext.getBuildArgs().entrySet()) {
        buildCommandLine.addAll(
            List.of("--build-arg", buildArg.getKey() + "=" + buildArg.getValue()));
      }
    }
    if (!ext.getLabels().isEmpty()) {
      for (Map.Entry<String, String> label : ext.getLabels().entrySet()) {
        if (!LABEL_KEY_PATTERN.matcher(label.getKey()).matches()) {
          throw new GradleException(
              String.format(
                  "Docker label '%s' contains illegal characters. Label keys must only contain"
                      + " lowercase alphanumeric, `.`, or `-` characters (must match %s).",
                  label.getKey(), LABEL_KEY_PATTERN.pattern()));
        }
        buildCommandLine.addAll(List.of("--label", label.getKey() + "=" + label.getValue()));
      }
    }
    if (ext.getPull()) {
      buildCommandLine.add("--pull");
    }
    buildCommandLine.addAll(List.of("-t", ext.getName(), "."));
    return buildCommandLine;
  }

  @Deprecated
  private static String computeName(String name, String tag) {
    int firstAt = tag.indexOf("@");
    String tagValue;
    if (firstAt > 0) {
      tagValue = tag.substring(firstAt + 1);
    } else {
      tagValue = tag;
    }
    if (tagValue.contains(":") || tagValue.contains("/")) {
      return tagValue;
    } else {
      int lastColon = name.lastIndexOf(":");
      int lastSlash = name.lastIndexOf("/");
      int endIndex;
      if (lastColon > lastSlash) {
        endIndex = lastColon;
      } else {
        endIndex = name.length();
      }
      return name.substring(0, endIndex) + ":" + tagValue;
    }
  }

  @Deprecated
  private static String generateTagTaskName(String name) {
    String tagTaskName = name;
    int firstAt = name.indexOf("@");
    if (firstAt > 0) {
      tagTaskName = name.substring(0, firstAt);
    } else if (firstAt == 0) {
      throw new GradleException("Task name of docker tag '" + name + "' must not be empty.");
    } else if (name.contains(":") || name.contains("/")) {
      throw new GradleException("Docker tag '" + name + "' must have a task name.");
    }
    return tagTaskName.substring(0, 1).toUpperCase() + tagTaskName.substring(1);
  }

  private record TagConfig(String tagName, Supplier<String> tagTask) {}
}
