package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.mavencentralpublish.MavenCentralPublishExtension;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ConfigureMavenCentralPomFeature implements PluginFeature {
  public static final String PUBLICATION_NAME = "mavenJava";
  public static final String STAGING_REPOSITORY_NAME = "stagingDeploy";

  @Override
  public void apply(Project project) {
    if (!project.getPlugins().hasPlugin(MavenPublishPlugin.class)) {
      project.getPlugins().apply(MavenPublishPlugin.class);
    }

    project.afterEvaluate(evaluatedProject -> configurePublication(evaluatedProject));
  }

  private void configurePublication(Project project) {
    MavenCentralPublishExtension extension =
        project.getExtensions().getByType(MavenCentralPublishExtension.class);
    validate(project, extension);

    PublishingExtension publishingExtension =
        project.getExtensions().getByType(PublishingExtension.class);

    publishingExtension.publications(
        publications ->
            publications.create(
                PUBLICATION_NAME,
                MavenPublication.class,
                publication -> {
                  publication.from(project.getComponents().getByName(extension.getComponentName()));
                  publication.pom(
                      pom -> {
                        pom.getName().set(extension.getPomName());
                        pom.getDescription().set(extension.getPomDescription());
                        pom.getUrl().set(extension.getPomUrl());
                        pom.licenses(
                            licenses ->
                                licenses.license(
                                    license -> {
                                      license.getName().set(extension.getLicenseName());
                                      license.getUrl().set(extension.getLicenseUrl());
                                    }));
                        pom.developers(
                            developers ->
                                developers.developer(
                                    developer -> {
                                      developer.getId().set(extension.getDeveloperId());
                                      developer.getName().set(extension.getDeveloperName());
                                      developer.getEmail().set(extension.getDeveloperEmail());
                                    }));
                        pom.scm(
                            scm -> {
                              scm.getConnection().set(extension.getScmConnection());
                              scm.getDeveloperConnection()
                                  .set(extension.getScmDeveloperConnection());
                              scm.getUrl().set(extension.getScmUrl());
                            });
                      });
                  publication.versionMapping(
                      mapping -> {
                        mapping.usage(Usage.JAVA_API, strategy -> strategy.fromResolutionResult());
                        mapping.usage(
                            Usage.JAVA_RUNTIME, strategy -> strategy.fromResolutionResult());
                      });
                }));

    publishingExtension.repositories(
        repositories ->
            repositories.maven(
                repository -> {
                  repository.setName(STAGING_REPOSITORY_NAME);
                  repository.setUrl(
                      project
                          .getLayout()
                          .getBuildDirectory()
                          .dir("staging-deploy")
                          .get()
                          .getAsFile()
                          .toURI());
                }));
  }

  private void validate(Project project, MavenCentralPublishExtension extension) {
    requireNonNull(project, extension.getComponentName(), "componentName");
    requireNonNull(project, extension.getPomName(), "pomName");
    requireNonNull(project, extension.getPomDescription(), "pomDescription");
    requireNonNull(project, extension.getPomUrl(), "pomUrl");
    requireNonNull(project, extension.getLicenseName(), "licenseName");
    requireNonNull(project, extension.getLicenseUrl(), "licenseUrl");
    requireNonNull(project, extension.getDeveloperId(), "developerId");
    requireNonNull(project, extension.getDeveloperName(), "developerName");
    requireNonNull(project, extension.getDeveloperEmail(), "developerEmail");
    requireNonNull(project, extension.getScmConnection(), "scmConnection");
    requireNonNull(project, extension.getScmDeveloperConnection(), "scmDeveloperConnection");
    requireNonNull(project, extension.getScmUrl(), "scmUrl");
  }

  private void requireNonNull(Project project, String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new GradleException(
          String.format(
              "mavenCentralPublish.%s must be set on project '%s' (applied by"
                  + " com.stano.maven-central-publish)",
              fieldName, project.getName()));
    }
  }
}
