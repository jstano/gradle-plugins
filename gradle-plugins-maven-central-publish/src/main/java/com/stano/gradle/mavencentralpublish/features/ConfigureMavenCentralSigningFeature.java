package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

public class ConfigureMavenCentralSigningFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    if (!project.getPlugins().hasPlugin(SigningPlugin.class)) {
      project.getPlugins().apply(SigningPlugin.class);
    }

    project.afterEvaluate(
        evaluatedProject -> {
          SigningExtension signingExtension =
              evaluatedProject.getExtensions().getByType(SigningExtension.class);
          signingExtension.setRequired(
              evaluatedProject.getGradle().getTaskGraph().hasTask("publish"));
          PublishingExtension publishingExtension =
              evaluatedProject.getExtensions().getByType(PublishingExtension.class);
          signingExtension.sign(
              publishingExtension
                  .getPublications()
                  .getByName(ConfigureMavenCentralPomFeature.PUBLICATION_NAME));
        });
  }
}
