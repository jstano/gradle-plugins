package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ConfigurePublishFeature implements PluginFeature {
  private static final String MAVEN_CENTRAL_PUBLISH_PLUGIN_ID = "com.stano.maven-central-publish";

  @Override
  public void apply(Project project) {
    if (!project.getPlugins().hasPlugin(MavenPublishPlugin.class)) {
      return;
    }
    MavenRepositoryUtils.disableEnforcedPlatformError(project);
    // Deferred to afterEvaluate: com.stano.maven-central-publish may be applied later in the
    // plugins {} block, so the hasPlugin check below can only be trusted once the whole build
    // script has finished applying plugins. When both plugins are present, maven-central-publish
    // already registers its own "mavenJava" publication for the same group:artifact:version
    // coordinates, so creating this plugin's project-named publication too would make
    // publishToMavenLocal (and any shared remote repository) reject the build with a duplicate
    // publication error.
    project.afterEvaluate(
        evaluatedProject -> {
          if (evaluatedProject.getPlugins().hasPlugin(MAVEN_CENTRAL_PUBLISH_PLUGIN_ID)) {
            return;
          }
          MavenRepositoryUtils.configurePublishing(evaluatedProject);
        });
  }
}
