package com.stano.gradle.docker;

import com.google.common.collect.ImmutableSet;
import java.io.File;
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
          File buildDirFile = p.getLayout().getBuildDirectory().get().getAsFile();
          String dockerDir = buildDirFile + "/docker";
          clean.delete(dockerDir);
          prepare.from(ext.getCopySpec());
          prepare.from(
              ext.getResolvedDockerfile(),
              spec -> {
                spec.rename(fileName -> "Dockerfile");
              });
          prepare.into(dockerDir);
          exec.setWorkingDir(dockerDir);
          exec.dependsOn(ext.getDependencies());
          exec.getLogging().captureStandardOutput(LogLevel.INFO);
          exec.getLogging().captureStandardError(LogLevel.ERROR);

          boolean buildx = ext.getBuildx();
          Set<String> platform = ext.getPlatform();
          boolean noCache = ext.getNoCache();
          String network = ext.getNetwork();
          Map<String, String> buildArgs = ext.getBuildArgs();
          boolean pull = ext.getPull();
          boolean load = ext.getLoad();
          boolean push = ext.getPush();
          String builder = ext.getBuilder();
          java.util.function.Supplier<String> nameSupplier = ext.getNameSupplier();
          java.util.function.Supplier<Map<String, String>> labelsSupplier = ext.getLabelsSupplier();

          exec.doFirst(
              task ->
                  exec.commandLine(
                      buildCommandLine(
                          buildx,
                          platform,
                          noCache,
                          network,
                          buildArgs,
                          pull,
                          load,
                          push,
                          builder,
                          nameSupplier.get(),
                          labelsSupplier.get())));

          copyDockerImageUrl.doLast(
              ignored -> {
                String content = nameSupplier.get();
                String fileName = buildDirFile + "/docker-image-url.txt";
                try {
                  Files.writeString(Path.of(fileName), content);
                } catch (IOException e) {
                  throw new GradleException("Failed to write docker image URL file", e);
                }
                System.out.println("File created successfully: " + fileName);
              });

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
                new TagConfig(
                    unresolvedTagName, () -> computeName(nameSupplier.get(), computedTag)));
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
                          task.dependsOn(exec);
                          task.doFirst(
                              t ->
                                  task.commandLine(
                                      "docker",
                                      "tag",
                                      nameSupplier.get(),
                                      tagConfig.tagTask.get()));
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
                          task.dependsOn(tagSubTask);
                          task.doFirst(
                              t -> task.commandLine("docker", "push", tagConfig.tagTask.get()));
                        });
            pushAllTags.dependsOn(pushSubTask);
          }
          dockerfileZip.from(ext.getResolvedDockerfile());
        });
  }

  private List<String> buildCommandLine(
      boolean buildx,
      Set<String> platform,
      boolean noCache,
      String network,
      Map<String, String> buildArgs,
      boolean pull,
      boolean load,
      boolean push,
      String builder,
      String resolvedName,
      Map<String, String> resolvedLabels) {
    List<String> buildCommandLine = new ArrayList<>();
    buildCommandLine.add("docker");
    if (buildx) {
      buildCommandLine.addAll(List.of("buildx", "build"));
      Set<String> effectivePlatform =
          platform.isEmpty() ? ImmutableSet.of("linux/amd64") : platform;
      buildCommandLine.addAll(List.of("--platform", String.join(",", effectivePlatform)));
      buildCommandLine.add("--no-cache");
      buildCommandLine.add("--pull");
      if (load) {
        buildCommandLine.add("--load");
      }
      if (push) {
        buildCommandLine.add("--push");
        if (load) {
          throw new GradleException("cannot combine 'push' and 'load' options");
        }
      }
      if (builder != null) {
        buildCommandLine.addAll(List.of("--builder", builder));
      }
    } else {
      buildCommandLine.add("build");
    }
    if (noCache) {
      buildCommandLine.add("--no-cache");
    }
    if (network != null) {
      buildCommandLine.addAll(List.of("--network", network));
    }
    if (!buildArgs.isEmpty()) {
      for (Map.Entry<String, String> buildArg : buildArgs.entrySet()) {
        buildCommandLine.addAll(
            List.of("--build-arg", buildArg.getKey() + "=" + buildArg.getValue()));
      }
    }
    if (!resolvedLabels.isEmpty()) {
      for (Map.Entry<String, String> label : resolvedLabels.entrySet()) {
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
    if (pull) {
      buildCommandLine.add("--pull");
    }
    buildCommandLine.addAll(List.of("-t", resolvedName, "."));
    return buildCommandLine;
  }

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
