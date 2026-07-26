package com.stano.gradle.docker;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BranchNameProvider;
import com.stano.gradle.base.CommitHashProvider;
import com.stano.gradle.base.CommitTimeProvider;
import com.stano.gradle.base.GradlePluginUtil;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.base.RepositoryOrganizationProvider;
import com.stano.gradle.base.RepositoryUrlProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;

public class ConfigureTasks implements PluginFeature {
  @Override
  public void apply(Project project) {
    final var dockerRegistrySettings = new DockerRegistrySettings(project);
    final var baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    DockerRemoveImagesExtension dockerRemoveImagesExtension =
        project.getExtensions().create("dockerRemoveImages", DockerRemoveImagesExtension.class);
    configureDockerDefaults(project, dockerRegistrySettings, baseExtension);
    project.afterEvaluate(
        p -> {
          DockerExtension dockerExtension = p.getExtensions().getByType(DockerExtension.class);
          Supplier<String> nameSupplier = dockerExtension.getNameSupplier();
          Task loginTask = createDockerLoginTask(p, dockerRegistrySettings, baseExtension);
          Task logoutTask = createDockerLogoutTask(p, dockerRegistrySettings);
          Task cleanupImageTask = createDockerCleanupImageTask(p, nameSupplier);
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
      Project project, DockerRegistrySettings dockerRegistrySettings, BaseExtension baseExtension) {
    return project
        .getTasks()
        .register(
            "dockerLogin",
            Exec.class,
            exec -> {
              exec.setGroup("Docker");
              exec.setDescription("Logs in to docker");
              exec.doFirst(
                  task -> {
                    String registryHost = dockerRegistrySettings.getHost();
                    if (AwsEcrLoginCommandBuilder.isEcrRegistry(registryHost)) {
                      exec.commandLine(
                          AwsEcrLoginCommandBuilder.buildLoginCommand(
                              registryHost, baseExtension.getDockerRegistryAwsProfile()));
                    } else {
                      exec.commandLine(
                          DockerExecutable.resolve(),
                          "login",
                          "-u",
                          dockerRegistrySettings.getUsername(),
                          "-p",
                          dockerRegistrySettings.getPassword(),
                          registryHost);
                    }
                  });
            })
        .get();
  }

  private Task createDockerLogoutTask(
      Project project, DockerRegistrySettings dockerRegistrySettings) {
    return project
        .getTasks()
        .register(
            "dockerLogout",
            Exec.class,
            exec -> {
              exec.setGroup("Docker");
              exec.setDescription("Logs out of docker");
              exec.doFirst(
                  task ->
                      exec.commandLine(
                          DockerExecutable.resolve(), "logout", dockerRegistrySettings.getHost()));
            })
        .get();
  }

  private Task createDockerCleanupImageTask(Project project, Supplier<String> nameSupplier) {
    return project
        .getTasks()
        .register(
            "dockerCleanupImage",
            Exec.class,
            exec -> {
              exec.setGroup("Docker");
              exec.setDescription("Remove docker image");
              exec.doFirst(
                  task -> {
                    List<String> args =
                        new ArrayList<>(
                            Arrays.asList(
                                DockerExecutable.resolve(),
                                "image",
                                "rm",
                                "--force",
                                nameSupplier.get()));
                    exec.commandLine(args.toArray());
                  });
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
              List<String> args =
                  new ArrayList<>(Arrays.asList(DockerExecutable.resolve(), "image", "rm"));
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
      Project project, DockerRegistrySettings dockerRegistrySettings, BaseExtension baseExtension) {
    DockerExtension dockerExtension = project.getExtensions().getByType(DockerExtension.class);
    File gitRootDir = project.getRootDir();
    dockerExtension.setDefaultLabelsSupplier(
        buildDefaultLabelsSupplier(gitRootDir, baseExtension.getBuildNumber()));
    boolean hasSpringBootPlugin = project.getPlugins().hasPlugin("com.stano.spring-boot");
    if (hasSpringBootPlugin) {
      String contextName = baseExtension.getContextName();
      String registryHost = dockerRegistrySettings.getHost();
      String projectVersion = String.valueOf(project.getVersion());
      dockerExtension.setDefaultNameSupplier(
          () -> {
            boolean isLocalBuild = registryHost == null || registryHost.isEmpty();
            String prefix =
                isLocalBuild
                    ? ""
                    : registryHost + "/" + new RepositoryOrganizationProvider(gitRootDir) + "/";
            return String.format(
                "%s%s/%s:%s",
                prefix,
                contextName.toLowerCase(),
                new BranchNameProvider(gitRootDir).toString().toLowerCase(),
                projectVersion);
          });
      Task dockerDependencyTask = project.getTasks().getByName("bootJar");
      dockerExtension.files(project.files(dockerDependencyTask.getOutputs()));
      Task copyOtelJavaagentTask = project.getTasks().findByName("copyOtelJavaagent");
      if (copyOtelJavaagentTask != null) {
        dockerExtension
            .getCopySpec()
            .from(
                copyOtelJavaagentTask,
                spec -> spec.into("otel").include("opentelemetry-javaagent*.jar"));
      }
      dockerExtension.buildArgs(
          getStandardBuildArgs(project, contextName, dockerRegistrySettings, baseExtension));
    }
  }

  private Supplier<Map<String, String>> buildDefaultLabelsSupplier(
      File gitRootDir, String buildNumber) {
    return () -> {
      Map<String, String> labels = new HashMap<>();
      labels.put("com.stano.build-hostname", GradlePluginUtil.getHostName());
      labels.put("com.stano.build-username", System.getProperty("user.name"));
      String repositoryUrl = new RepositoryUrlProvider(gitRootDir).toString();
      String branchName = new BranchNameProvider(gitRootDir).toString();
      String commitHash = new CommitHashProvider(gitRootDir).toString();
      String commitTime = new CommitTimeProvider(gitRootDir).toString();
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
    };
  }

  private Map<String, String> getStandardBuildArgs(
      Project project,
      String contextName,
      DockerRegistrySettings dockerRegistrySettings,
      BaseExtension baseExtension) {
    Map<String, String> buildArgs = new HashMap<>();
    if (dockerRegistrySettings.getHost() != null) {
      buildArgs.put("DOCKER_REGISTRY", dockerRegistrySettings.getHost());
    }
    buildArgs.put("PROJECT_VERSION", project.getVersion().toString());
    buildArgs.put("CONTEXT_NAME", contextName);
    if (baseExtension.getBuildNumber() != null) {
      buildArgs.put("BUILD_NUMBER", baseExtension.getBuildNumber());
    }
    return buildArgs;
  }
}
