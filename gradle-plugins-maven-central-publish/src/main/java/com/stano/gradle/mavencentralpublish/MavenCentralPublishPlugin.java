package com.stano.gradle.mavencentralpublish;

import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralPomFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralPublishExtensionFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralSigningFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralStagingZipFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralUploadTaskFeature;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MavenCentralPublishPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    new ConfigureMavenCentralPublishExtensionFeature().apply(project);
    new ConfigureMavenCentralPomFeature().apply(project);
    new ConfigureMavenCentralSigningFeature().apply(project);
    new ConfigureMavenCentralStagingZipFeature().apply(project);
    new ConfigureMavenCentralUploadTaskFeature().apply(project);
  }
}
