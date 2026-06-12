package com.stano.gradle.docker;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;

public class DockerComponent implements SoftwareComponentInternal {
  private final Set<PublishArtifact> artifacts = new LinkedHashSet<>();

  public DockerComponent(PublishArtifact dockerArtifact) {
    artifacts.add(dockerArtifact);
  }

  @Override
  public final String getName() {
    return "docker";
  }

  @Override
  public final Set<UsageContext> getUsages() {
    return Collections.emptySet();
  }
}
