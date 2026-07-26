package com.stano.gradle.docker;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.CopySpec;

/**
 * Never capture this extension instance inside a Task action (doFirst/doLast) — it holds a live
 * Project reference and is not configuration-cache-safe. Capture the result of {@link
 * #getNameSupplier()} / {@link #getLabelsSupplier()} (or plain values) as local variables before
 * building any execution-time closure.
 */
public class DockerExtension {
  private static final String DEFAULT_DOCKERFILE_PATH = "Dockerfile";
  private transient Project project;
  private String name;
  private Supplier<String> defaultNameSupplier;
  private File dockerfile;
  private Set<Task> dependencies = ImmutableSet.of();
  private Set<String> tags = ImmutableSet.of();
  private Map<String, String> namedTags = new HashMap<>();
  private Map<String, String> labels = ImmutableMap.of();
  private Supplier<Map<String, String>> defaultLabelsSupplier;
  private boolean labelsExplicitlySet = false;
  private Map<String, String> buildArgs = ImmutableMap.of();
  private boolean pull = false;
  private boolean noCache = false;
  private String network;
  private boolean buildx = true;
  private Set<String> platform = ImmutableSet.of();
  private boolean load = false;
  private boolean loadExplicitlySet = false;
  private boolean push = false;
  private String builder;
  private File resolvedDockerfile;
  private File resolvedDockerComposeTemplate;
  private File resolvedDockerComposeFile;
  private final CopySpec copySpec;

  public DockerExtension(Project project) {
    this.project = project;
    this.copySpec = project.copySpec();
  }

  public void setName(String name) {
    this.name = name;
  }

  void setDefaultNameSupplier(Supplier<String> defaultNameSupplier) {
    this.defaultNameSupplier = defaultNameSupplier;
  }

  public Supplier<String> getNameSupplier() {
    String explicitName = this.name;
    Supplier<String> fallback = this.defaultNameSupplier;
    return () -> {
      if (!Strings.isNullOrEmpty(explicitName)) {
        return explicitName;
      }
      Preconditions.checkArgument(
          fallback != null, "name is a required docker configuration item.");
      String resolved = fallback.get();
      Preconditions.checkArgument(
          !Strings.isNullOrEmpty(resolved), "name is a required docker configuration item.");
      return resolved;
    };
  }

  public String getName() {
    return getNameSupplier().get();
  }

  public void setDockerfile(File dockerfile) {
    this.dockerfile = dockerfile;
  }

  public void setDockerComposeTemplate(String dockerComposeTemplate) {
    this.resolvedDockerComposeTemplate = project.file(dockerComposeTemplate);
    Preconditions.checkArgument(
        project.file(dockerComposeTemplate).exists(),
        "Could not find specified template file: %s",
        project.file(dockerComposeTemplate));
  }

  public void setDockerComposeFile(String dockerComposeFile) {
    this.resolvedDockerComposeFile = project.file(dockerComposeFile);
  }

  public void dependsOn(Task... args) {
    this.dependencies = ImmutableSet.copyOf(args);
  }

  public Set<Task> getDependencies() {
    return dependencies;
  }

  public void files(Object... files) {
    copySpec.from(files);
  }

  public Set<String> getTags() {
    String versionTag = project.getVersion().toString();
    if (versionTag != null
        && !versionTag.isEmpty()
        && !versionTag.contains(":")
        && !versionTag.contains("/")) {
      return Sets.union(this.tags, ImmutableSet.of(versionTag));
    }
    return ImmutableSet.copyOf(this.tags);
  }

  @Deprecated
  public void tags(String... args) {
    this.tags = ImmutableSet.copyOf(args);
  }

  public Map<String, String> getNamedTags() {
    return ImmutableMap.copyOf(namedTags);
  }

  public void tag(String taskName, String tag) {
    if (namedTags.putIfAbsent(taskName, tag) != null) {
      System.err.println("WARNING: Task name '" + taskName + "' is existed.");
    }
  }

  public Map<String, String> getLabels() {
    return getLabelsSupplier().get();
  }

  void setDefaultLabelsSupplier(Supplier<Map<String, String>> defaultLabelsSupplier) {
    this.defaultLabelsSupplier = defaultLabelsSupplier;
  }

  public Supplier<Map<String, String>> getLabelsSupplier() {
    boolean explicit = this.labelsExplicitlySet;
    Map<String, String> explicitLabels = this.labels;
    Supplier<Map<String, String>> fallback = this.defaultLabelsSupplier;
    return () -> (explicit || fallback == null) ? explicitLabels : fallback.get();
  }

  public void labels(Map<String, String> labels) {
    this.labels = ImmutableMap.copyOf(labels);
    this.labelsExplicitlySet = true;
  }

  public File getResolvedDockerfile() {
    return resolvedDockerfile;
  }

  public File getResolvedDockerComposeTemplate() {
    return resolvedDockerComposeTemplate;
  }

  public File getResolvedDockerComposeFile() {
    return resolvedDockerComposeFile;
  }

  public CopySpec getCopySpec() {
    return copySpec;
  }

  public void resolvePathsAndValidate() {
    if (dockerfile != null) {
      resolvedDockerfile = dockerfile;
    } else {
      resolvedDockerfile = project.file(DEFAULT_DOCKERFILE_PATH);
    }
    resolvedDockerComposeFile = project.file("docker-compose.yml");
    resolvedDockerComposeTemplate = project.file("docker-compose.yml.template");
  }

  public Map<String, String> getBuildArgs() {
    return buildArgs;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public void buildArgs(Map<String, String> buildArgs) {
    this.buildArgs = ImmutableMap.copyOf(buildArgs);
  }

  public boolean getPull() {
    return pull;
  }

  public void pull(boolean pull) {
    this.pull = pull;
  }

  public boolean getNoCache() {
    return noCache;
  }

  public void noCache(boolean noCache) {
    this.noCache = noCache;
  }

  public boolean getLoad() {
    return loadExplicitlySet ? load : !push;
  }

  public void load(boolean load) {
    this.load = load;
    this.loadExplicitlySet = true;
  }

  public boolean getPush() {
    return push;
  }

  public void push(boolean push) {
    this.push = push;
  }

  public boolean getBuildx() {
    return buildx;
  }

  public void buildx(boolean buildx) {
    this.buildx = buildx;
  }

  public Set<String> getPlatform() {
    return platform;
  }

  public void platform(String... args) {
    this.platform = ImmutableSet.copyOf(args);
  }

  public String getBuilder() {
    return builder;
  }

  public void builder(String builder) {
    this.builder = builder;
  }
}
