package com.stano.gradle.npm;

import com.stano.gradle.base.Environment;
import com.stano.gradle.base.SystemEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Internal;
import org.gradle.process.ExecOperations;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "abstract base class is never registered as a task directly")
public class BaseNpmTask extends DefaultTask {
  protected final File projectDir;
  protected final NpmExtension npmExtension;
  private final ExecOperations execOperations;
  private final Map<String, String> environment;
  private final List<String> arguments;

  @Internal
  public Map<String, String> getEnvironment() {
    return Collections.unmodifiableMap(environment);
  }

  public void addEnvironmentVariable(String key, String value) {
    environment.put(key, value);
  }

  @Internal
  public List<String> getArguments() {
    return arguments;
  }

  public void exec() {
    boolean useNvm = npmExtension.getUseNvm().get();
    new NpmExecRunner(
            execOperations,
            projectDir,
            useNvm,
            npmExtension.getNodeVersion().get(),
            environment,
            arguments)
        .run();
  }

  @Inject
  protected BaseNpmTask(
      Project project,
      ExecOperations execOperations,
      String group,
      String description,
      String... arguments) {
    setGroup(group);
    setDescription(description);

    npmExtension = project.getExtensions().getByType(NpmExtension.class);
    this.projectDir = npmExtension.getProjectDirectory().get();
    this.execOperations = execOperations;
    this.environment = getDefaultNodeEnvironment();
    this.arguments = Arrays.asList(arguments);
  }

  protected void setCIToTrueInRunnerEnvironment() {
    addEnvironmentVariable("CI", "true");
  }

  public static Map<String, String> getDefaultNodeEnvironment() {
    return getDefaultNodeEnvironment(new SystemEnvironment());
  }

  static Map<String, String> getDefaultNodeEnvironment(Environment environment) {
    Map<String, String> result = new HashMap<>(environment.getAllEnvironmentVariables());

    String nodeOptions = result.get("NODE_OPTIONS");

    if (nodeOptions == null || nodeOptions.trim().isEmpty()) {
      result.put("NODE_OPTIONS", "--max_old_space_size=4096");
    } else if (!nodeOptions.contains("--max_old_space_size")) {
      result.put("NODE_OPTIONS", nodeOptions + " --max_old_space_size=4096");
    }

    return result;
  }
}
