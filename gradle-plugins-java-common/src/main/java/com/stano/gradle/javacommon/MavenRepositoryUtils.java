package com.stano.gradle.javacommon;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;
import org.gradle.authentication.http.HttpHeaderAuthentication;
import org.gradle.jvm.tasks.Jar;

public class MavenRepositoryUtils {
  private static final String STANO_MAVEN_URL_PROPERTY = "com.stano.maven.url";
  private static final String STANO_MAVEN_USERNAME_PROPERTY = "com.stano.maven.username";
  private static final String STANO_MAVEN_PASSWORD_PROPERTY = "com.stano.maven.password";
  private static final String STANO_MAVEN_URL_ENVIRONMENT = "STANO_MAVEN_URL";
  private static final String STANO_MAVEN_USERNAME_ENVIRONMENT = "STANO_MAVEN_USERNAME";
  private static final String STANO_MAVEN_PASSWORD_ENVIRONMENT = "STANO_MAVEN_PASSWORD";

  public static void configureStanoMavenRepository(Project project, MavenArtifactRepository repository) {
    var properties = project.getExtensions().getExtraProperties().getProperties();
    var stanoMavenUrl = properties.containsKey(STANO_MAVEN_URL_PROPERTY)
                        ? properties.get(STANO_MAVEN_URL_PROPERTY).toString()
                        : System.getenv(STANO_MAVEN_URL_ENVIRONMENT);

    repository.setName("stano-maven");
    repository.setUrl(stanoMavenUrl);

    setRepositoryCredentials(project, repository);
  }

  public static void configurePublishing(Project project) {
    configurePublishingRepositories(project);

    PublishingExtension publishingExtension = project.getExtensions().findByType(PublishingExtension.class);
    publishingExtension.publications(publicationContainer -> {
      Jar jarTask = (Jar)project.getTasks().findByName("jar");

      publicationContainer.create(jarTask.getArchiveBaseName().get(), MavenPublication.class, publication -> {
        publication.from(project.getComponents().findByName("java"));
        publication.setArtifactId(jarTask.getArchiveBaseName().get());
      });
    });
  }

  public static void configurePublishingRepositories(Project project) {
    PublishingExtension publishingExtension = project.getExtensions().findByType(PublishingExtension.class);
    publishingExtension.repositories(repositoryHandler -> repositoryHandler.maven(repository -> {
      configureStanoMavenRepository(project, repository);
    }));
  }

  public static void disableEnforcedPlatformError(Project project) {
    // The value 'enforced-platform' is provided in the validation error message you got
    project.getTasks()
           .withType(GenerateModuleMetadata.class)
           .configureEach(generateModuleMetadata -> generateModuleMetadata.getSuppressedValidationErrors().add("enforced-platform"));
  }

  private static void setRepositoryCredentials(Project project, MavenArtifactRepository repository) {
    var properties = project.getExtensions().getExtraProperties().getProperties();
    var username = properties.containsKey(STANO_MAVEN_USERNAME_PROPERTY)
                   ? properties.get(STANO_MAVEN_USERNAME_PROPERTY).toString()
                   : System.getenv(STANO_MAVEN_USERNAME_ENVIRONMENT);
    var password = properties.containsKey(STANO_MAVEN_PASSWORD_PROPERTY)
                   ? properties.get(STANO_MAVEN_PASSWORD_PROPERTY).toString()
                   : System.getenv(STANO_MAVEN_PASSWORD_ENVIRONMENT);

    var credentials = repository.getCredentials(PasswordCredentials.class);
    credentials.setUsername(username);
    credentials.setPassword(password);

    repository.authentication(auth -> auth.create("header", HttpHeaderAuthentication.class, header -> {
    }));
  }
}
