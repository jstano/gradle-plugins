package com.stano.gradle.docker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;

public class DockerRunPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    DockerRunExtension ext = project.getExtensions().create("dockerRun", DockerRunExtension.class);
    Exec dockerRunStatus =
        project
            .getTasks()
            .create(
                "dockerRunStatus",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Checks the run status of the container");
                });
    Exec dockerRun =
        project
            .getTasks()
            .create(
                "dockerRun",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Runs the specified container with port mappings");
                });
    Exec dockerStop =
        project
            .getTasks()
            .create(
                "dockerStop",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Stops the named container if it is running");
                  task.setIgnoreExitValue(true);
                });
    Exec dockerRemoveContainer =
        project
            .getTasks()
            .create(
                "dockerRemoveContainer",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription(
                      "Removes the persistent container associated with the Docker Run tasks");
                  task.setIgnoreExitValue(true);
                });
    Exec dockerNetworkModeStatus =
        project
            .getTasks()
            .create(
                "dockerNetworkModeStatus",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Checks the network configuration of the container");
                });
    project.afterEvaluate(
        p -> {
          dockerRunStatus.setStandardOutput(new ByteArrayOutputStream());
          dockerRunStatus.commandLine(
              "docker", "inspect", "--format={{.State.Running}}", ext.getName());
          dockerRunStatus.doLast(
              task -> {
                if (!"true".equals(dockerRunStatus.getStandardOutput().toString().trim())) {
                  System.out.println("Docker container '" + ext.getName() + "' is STOPPED.");
                } else {
                  System.out.println("Docker container '" + ext.getName() + "' is RUNNING.");
                }
              });
          dockerNetworkModeStatus.setStandardOutput(new ByteArrayOutputStream());
          dockerNetworkModeStatus.commandLine(
              "docker", "inspect", "--format={{.HostConfig.NetworkMode}}", ext.getName());
          dockerNetworkModeStatus.doLast(
              task -> {
                String networkMode = dockerNetworkModeStatus.getStandardOutput().toString().trim();
                if ("default".equals(networkMode)) {
                  System.out.println(
                      "Docker container '"
                          + ext.getName()
                          + "' has default network configuration (bridge).");
                } else if (networkMode.equals(ext.getNetwork())) {
                  System.out.println(
                      "Docker container '"
                          + ext.getName()
                          + "' is configured to run with '"
                          + ext.getNetwork()
                          + "' network mode.");
                } else {
                  System.out.println(
                      "Docker container '"
                          + ext.getName()
                          + "' runs with '"
                          + networkMode
                          + "' network mode instead of the configured '"
                          + ext.getNetwork()
                          + "'.");
                }
              });
          List<String> args = new ArrayList<>();
          args.addAll(List.of("docker", "run"));
          dockerRun.setIgnoreExitValue(ext.getIgnoreExitValue());
          if (ext.getDaemonize()) {
            args.add("-d");
          }
          if (ext.getClean()) {
            args.add("--rm");
          } else {
            dockerRun.finalizedBy(dockerRunStatus);
          }
          if (ext.getNetwork() != null) {
            args.add("--network");
            args.add(ext.getNetwork());
          }
          for (String port : ext.getPorts()) {
            args.add("-p");
            args.add(port);
          }
          for (Map.Entry<Object, String> volume : ext.getVolumes().entrySet()) {
            File localFile = p.file(volume.getKey());
            if (!localFile.exists()) {
              System.err.println(
                  "ERROR: Local folder "
                      + localFile
                      + " doesn't exist. Mounted volume will not be visible to container");
              throw new IllegalStateException("Local folder " + localFile + " doesn't exist.");
            }
            args.add("-v");
            args.add(localFile.getAbsolutePath() + ":" + volume.getValue());
          }
          for (Map.Entry<String, String> e : ext.getEnv().entrySet()) {
            args.add("-e");
            args.add(e.getKey() + "=" + e.getValue());
          }
          args.add("--name");
          args.add(ext.getName());
          if (!ext.getArguments().isEmpty()) {
            args.addAll(ext.getArguments());
          }
          args.add(ext.getImage());
          if (!ext.getCommand().isEmpty()) {
            args.addAll(ext.getCommand());
          }
          dockerRun.commandLine(args);
          dockerStop.commandLine("docker", "stop", ext.getName());
          dockerRemoveContainer.commandLine("docker", "rm", ext.getName());
        });
  }
}
