package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.MavenRepositoryCredentials;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;
import org.gradle.jvm.tasks.Jar;

public class MavenRepositoryUtils {
  private static final String STANO_MAVEN_URL_PROPERTY = "com.stano.maven.url";
  private static final String STANO_MAVEN_URL_ENVIRONMENT = "STANO_MAVEN_URL";

  public static void configureStanoMavenRepository(
      Project project, MavenArtifactRepository repository) {
    var stanoMavenUrl = resolveStanoMavenUrl(project);
    if (stanoMavenUrl == null) {
      return;
    }
    repository.setName("stano-maven");
    repository.setUrl(stanoMavenUrl);
    var properties = project.getExtensions().getExtraProperties().getProperties();
    MavenRepositoryCredentials.configureCredentials(properties, repository);
  }

  public static void configurePublishing(Project project) {
    if (resolveStanoMavenUrl(project) == null) {
      return;
    }
    configurePublishingRepositories(project);
    PublishingExtension publishingExtension =
        project.getExtensions().findByType(PublishingExtension.class);
    publishingExtension.publications(
        publicationContainer -> {
          Jar jarTask = (Jar) project.getTasks().findByName("jar");
          publicationContainer.create(
              jarTask.getArchiveBaseName().get(),
              MavenPublication.class,
              publication -> {
                publication.from(project.getComponents().findByName("java"));
                publication.setArtifactId(jarTask.getArchiveBaseName().get());
              });
        });
  }

  public static void configurePublishingRepositories(Project project) {
    if (resolveStanoMavenUrl(project) == null) {
      return;
    }
    PublishingExtension publishingExtension =
        project.getExtensions().findByType(PublishingExtension.class);
    publishingExtension.repositories(
        repositoryHandler ->
            repositoryHandler.maven(
                repository -> {
                  configureStanoMavenRepository(project, repository);
                }));
  }

  private static String resolveStanoMavenUrl(Project project) {
    var properties = project.getExtensions().getExtraProperties().getProperties();
    return properties.containsKey(STANO_MAVEN_URL_PROPERTY)
        ? properties.get(STANO_MAVEN_URL_PROPERTY).toString()
        : System.getenv(STANO_MAVEN_URL_ENVIRONMENT);
  }

  public static void disableEnforcedPlatformError(Project project) {
    // The value 'enforced-platform' is provided in the validation error message you got
    project
        .getTasks()
        .withType(GenerateModuleMetadata.class)
        .configureEach(
            generateModuleMetadata ->
                generateModuleMetadata.getSuppressedValidationErrors().add("enforced-platform"));
  }
}
