package com.stano.gradle.docker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;

public class DockerRunPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    DockerRunExtension ext = project.getExtensions().create("dockerRun", DockerRunExtension.class);
    TaskProvider<Exec> dockerRunStatus =
        project
            .getTasks()
            .register(
                "dockerRunStatus",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Checks the run status of the container");
                });
    TaskProvider<Exec> dockerRun =
        project
            .getTasks()
            .register(
                "dockerRun",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Runs the specified container with port mappings");
                });
    TaskProvider<Exec> dockerStop =
        project
            .getTasks()
            .register(
                "dockerStop",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Stops the named container if it is running");
                  task.setIgnoreExitValue(true);
                });
    TaskProvider<Exec> dockerRemoveContainer =
        project
            .getTasks()
            .register(
                "dockerRemoveContainer",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription(
                      "Removes the persistent container associated with the Docker Run tasks");
                  task.setIgnoreExitValue(true);
                });
    TaskProvider<Exec> dockerNetworkModeStatus =
        project
            .getTasks()
            .register(
                "dockerNetworkModeStatus",
                Exec.class,
                task -> {
                  task.setGroup("Docker Run");
                  task.setDescription("Checks the network configuration of the container");
                });
    project.afterEvaluate(
        p -> {
          dockerRunStatus.configure(
              task -> {
                task.setStandardOutput(new ByteArrayOutputStream());
                task.commandLine("docker", "inspect", "--format={{.State.Running}}", ext.getName());
                task.doLast(
                    t -> {
                      if (!"true".equals(task.getStandardOutput().toString().trim())) {
                        System.out.println("Docker container '" + ext.getName() + "' is STOPPED.");
                      } else {
                        System.out.println("Docker container '" + ext.getName() + "' is RUNNING.");
                      }
                    });
              });
          dockerNetworkModeStatus.configure(
              task -> {
                task.setStandardOutput(new ByteArrayOutputStream());
                task.commandLine(
                    "docker", "inspect", "--format={{.HostConfig.NetworkMode}}", ext.getName());
                task.doLast(
                    t -> {
                      String networkMode = task.getStandardOutput().toString().trim();
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
              });
          dockerRun.configure(
              task -> {
                List<String> args = new ArrayList<>();
                args.addAll(List.of("docker", "run"));
                task.setIgnoreExitValue(ext.getIgnoreExitValue());
                if (ext.getDaemonize()) {
                  args.add("-d");
                }
                if (ext.getClean()) {
                  args.add("--rm");
                } else {
                  task.finalizedBy(dockerRunStatus);
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
                    throw new IllegalStateException(
                        "Local folder " + localFile + " doesn't exist.");
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
                task.commandLine(args);
              });
          dockerStop.configure(task -> task.commandLine("docker", "stop", ext.getName()));
          dockerRemoveContainer.configure(task -> task.commandLine("docker", "rm", ext.getName()));
        });
  }
}
