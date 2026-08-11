package com.stano.gradle.javalibrary.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.stano.gradle.base.BasePluginTest;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.HttpHeaderCredentials;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.junit.jupiter.api.Test;

class MavenRepositoryUtilsTest extends BasePluginTest {
  @Test
  void configurePublishingShouldNotCreateAPublicationWhenStanoMavenUrlIsNotConfigured() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);

    MavenRepositoryUtils.configurePublishing(childProject);

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertTrue(publishingExtension.getPublications().isEmpty());
  }

  @Test
  void configurePublishingShouldNotAddARepositoryWhenStanoMavenUrlIsNotConfigured() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);

    MavenRepositoryUtils.configurePublishing(childProject);

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertTrue(publishingExtension.getRepositories().isEmpty());
  }

  @Test
  void configurePublishingShouldCreateAPublicationWhenStanoMavenUrlIsConfigured() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");

    MavenRepositoryUtils.configurePublishing(childProject);

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertFalse(publishingExtension.getPublications().isEmpty());
    assertTrue(
        publishingExtension.getPublications().stream()
            .anyMatch(publication -> publication.getName().equals(childProject.getName())));
  }

  @Test
  void configurePublishingShouldAddTheStanoMavenRepositoryWhenStanoMavenUrlIsConfigured() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");

    MavenRepositoryUtils.configurePublishing(childProject);

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertTrue(
        publishingExtension.getRepositories().stream()
            .anyMatch(repository -> repository.getName().equals("stano-maven")));
  }

  @Test
  void configurePublishingWithATokenConfiguredShouldUseHttpHeaderCredentials() {
    // CI_JOB_TOKEN always wins when present, which would invalidate this test's assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");
    childProject.getExtensions().getExtraProperties().set("com.stano.maven.token", "abc123");

    MavenRepositoryUtils.configurePublishing(childProject);

    var credentials = stanoMavenRepository().getCredentials(HttpHeaderCredentials.class);
    assertEquals("Private-Token", credentials.getName());
    assertEquals("abc123", credentials.getValue());
  }

  @Test
  void configurePublishingWithATokenAndCustomHeaderNameConfiguredShouldUseTheCustomHeaderName() {
    // CI_JOB_TOKEN always wins when present, which would invalidate this test's assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");
    childProject.getExtensions().getExtraProperties().set("com.stano.maven.token", "abc123");
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.token-header", "Job-Token");

    MavenRepositoryUtils.configurePublishing(childProject);

    var credentials = stanoMavenRepository().getCredentials(HttpHeaderCredentials.class);
    assertEquals("Job-Token", credentials.getName());
  }

  @Test
  void configurePublishingWithoutATokenButWithUsernameAndPasswordShouldUsePasswordCredentials() {
    // CI_JOB_TOKEN always wins when present, which would invalidate this test's assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply(MavenPublishPlugin.class);
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");
    childProject.getExtensions().getExtraProperties().set("com.stano.maven.username", "user");
    childProject.getExtensions().getExtraProperties().set("com.stano.maven.password", "pass");

    MavenRepositoryUtils.configurePublishing(childProject);

    var credentials = stanoMavenRepository().getCredentials(PasswordCredentials.class);
    assertEquals("user", credentials.getUsername());
    assertEquals("pass", credentials.getPassword());
  }

  private MavenArtifactRepository stanoMavenRepository() {
    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    return publishingExtension.getRepositories().stream()
        .filter(repository -> repository.getName().equals("stano-maven"))
        .map(MavenArtifactRepository.class::cast)
        .findFirst()
        .orElseThrow();
  }
}
