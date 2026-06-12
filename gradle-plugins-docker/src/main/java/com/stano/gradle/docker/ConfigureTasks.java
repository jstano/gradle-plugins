package com.stano.gradle.docker;

import com.stano.gradle.GradlePluginUtil;
import com.stano.gradle.PluginFeature;
import com.stano.gradle.RootExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;

public class ConfigureTasks implements PluginFeature {
  @Override
  public void apply(Project project) {
    final var dockerRegistrySettings = new DockerRegistrySettings(project);
    final var rootExtension = project.getExtensions().getByType(RootExtension.class);
    DockerRemoveImagesExtension dockerRemoveImagesExtension =
        project.getExtensions().create("dockerRemoveImages", DockerRemoveImagesExtension.class);
    configureDockerDefaults(project, dockerRegistrySettings, rootExtension);
    project.afterEvaluate(
        p -> {
          Task loginTask = createDockerLoginTask(p, dockerRegistrySettings);
          Task logoutTask = createDockerLogoutTask(p, dockerRegistrySettings);
          Task cleanupImageTask = createDockerCleanupImageTask(p);
          Collection<String> dockerRemoveImages =
              dockerRemoveImagesExtension.getImages().getOrElse(Collections.emptyList());
          if (!dockerRemoveImages.isEmpty()) {
            createDockerRemoveImagesTask(p).dependsOn(p.getTasks().getByName("docker"));
          }
          Task dockerPushTask = p.getTasks().getByName("dockerPush");
          dockerPushTask.dependsOn(loginTask);
          cleanupImageTask.dependsOn(dockerPushTask);
          logoutTask.dependsOn(cleanupImageTask);
          Task dockerTagsPushTask = p.getTasks().getByName("dockerTagsPush");
          dockerTagsPushTask.dependsOn(loginTask);
          dockerTagsPushTask.finalizedBy(logoutTask);
          project.getTasks().getByName("dockerfileZip").setEnabled(false);
        });
  }

  private Task createDockerLoginTask(
      Project project, DockerRegistrySettings dockerRegistrySettings) {
    String registryHost = dockerRegistrySettings.getHost();
    if (registryHost.endsWith(".amazonaws.com")) {
      // 322095465246.dkr.ecr.us-east-2.amazonaws.com
      // aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS
      // --password-stdin $ECR_HOST
      String awsRegion =
          registryHost.replaceAll(".*\\.dkr\\.ecr\\.", "").replace(".amazonaws.com", "");
      String awsDockerLogin =
          String.format(
              "aws ecr get-login-password --region %s | docker login --username AWS"
                  + " --password-stdin %s",
              awsRegion, registryHost);
      return project
          .getTasks()
          .register(
              "dockerLogin",
              Exec.class,
              exec -> {
                exec.commandLine("bash", "-c", awsDockerLogin);
                exec.setGroup("Docker");
                exec.setDescription("Logs in to docker");
              })
          .get();
    }
    return project
        .getTasks()
        .register(
            "dockerLogin",
            Exec.class,
            exec -> {
              exec.commandLine(
                  "docker",
                  "login",
                  "-u",
                  dockerRegistrySettings.getUsername(),
                  "-p",
                  dockerRegistrySettings.getPassword(),
                  registryHost);
              exec.setGroup("Docker");
              exec.setDescription("Logs in to docker");
            })
        .get();
  }

  private Task createDockerLogoutTask(
      Project project, DockerRegistrySettings dockerRegistrySettings) {
    String registryHost = dockerRegistrySettings.getHost();
    return project
        .getTasks()
        .register(
            "dockerLogout",
            Exec.class,
            exec -> {
              exec.commandLine("docker", "logout", registryHost);
              exec.setGroup("Docker");
              exec.setDescription("Logs out of docker");
            })
        .get();
  }

  private Task createDockerCleanupImageTask(Project project) {
    DockerExtension dockerExtension = project.getExtensions().getByType(DockerExtension.class);
    return project
        .getTasks()
        .register(
            "dockerCleanupImage",
            Exec.class,
            exec -> {
              List<String> args =
                  new ArrayList<>(
                      Arrays.asList("docker", "image", "rm", "--force", dockerExtension.getName()));
              exec.commandLine(args.toArray());
              exec.setGroup("Docker");
              exec.setDescription("Remove docker image");
            })
        .get();
  }

  private Task createDockerRemoveImagesTask(Project project) {
    return project
        .getTasks()
        .register(
            "dockerRemoveImages",
            Exec.class,
            exec -> {
              DockerRemoveImagesExtension extension =
                  project.getExtensions().findByType(DockerRemoveImagesExtension.class);
              Collection<String> images = extension.getImages().getOrElse(Collections.emptyList());
              boolean force = extension.getForce().getOrElse(false);
              boolean noPrune = extension.getNoPrune().getOrElse(false);
              List<String> args = new ArrayList<>(Arrays.asList("docker", "image", "rm"));
              if (force) {
                args.add("--force");
              }
              if (noPrune) {
                args.add("--no-prune");
              }
              args.addAll(images);
              exec.commandLine(args.toArray());
              exec.setGroup("Docker");
              exec.setDescription("Removes local docker images");
            })
        .get();
  }

  private void configureDockerDefaults(
      Project project, DockerRegistrySettings dockerRegistrySettings, RootExtension rootExtension) {
    DockerExtension dockerExtension = project.getExtensions().getByType(DockerExtension.class);
    dockerExtension.labels(getStandardLabels(project, rootExtension));
    boolean hasSpringBootPlugin = project.getPlugins().hasPlugin("com.stano.spring-boot");
    if (hasSpringBootPlugin) {
      String contextName = rootExtension.getContextName();
      dockerExtension.setName(
          String.format(
              "%s/%s/%s/%s:%s",
              dockerRegistrySettings.getHost(),
              rootExtension.getRepositoryOrganizationProvider().toString(),
              contextName.toLowerCase(),
              rootExtension.getBranchNameProvider().toString().toLowerCase(),
              project.getVersion()));
      Task dockerDependencyTask = project.getTasks().getByName("bootWar");
      dockerExtension.files(dockerDependencyTask.getOutputs());
      dockerExtension.buildArgs(
          getStandardBuildArgs(project, contextName, dockerRegistrySettings, rootExtension));
    }
  }

  private Map<String, String> getStandardLabels(Project project, RootExtension rootExtension) {
    Map<String, String> labels = new HashMap<>();
    labels.put("com.stano.build-hostname", GradlePluginUtil.getHostName());
    labels.put("com.stano.build-username", System.getProperty("user.name"));
    String repositoryUrl = rootExtension.getRepositoryUrlProvider().toString();
    String branchName = rootExtension.getBranchNameProvider().toString();
    String buildNumber = rootExtension.getBuildNumber();
    String commitHash = rootExtension.getCommitHashProvider().toString();
    String commitTime = rootExtension.getCommitTimeProvider().toString();
    if (repositoryUrl != null) {
      labels.put("com.stano.repository-url", repositoryUrl);
    }
    if (branchName != null) {
      labels.put("com.stano.branch", branchName);
    }
    if (buildNumber != null) {
      labels.put("com.stano.build-number", buildNumber);
    }
    if (commitHash != null) {
      labels.put("com.stano.commit-hash", commitHash);
    }
    if (commitTime != null) {
      labels.put("com.stano.commit-time", commitTime);
    }
    return labels;
  }

  private Map<String, String> getStandardBuildArgs(
      Project project,
      String contextName,
      DockerRegistrySettings dockerRegistrySettings,
      RootExtension rootExtension) {
    Map<String, String> buildArgs = new HashMap<>();
    buildArgs.put("DOCKER_REGISTRY", dockerRegistrySettings.getHost());
    buildArgs.put("PROJECT_VERSION", project.getVersion().toString());
    buildArgs.put("CONTEXT_NAME", contextName);
    if (rootExtension.getBuildNumber() != null) {
      buildArgs.put("BUILD_NUMBER", rootExtension.getBuildNumber());
    }
    return buildArgs;
  }
}
