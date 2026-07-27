package com.stano.gradle.javalibrary.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
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
}
