package com.stano.gradle.javalibrary.features;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.mavencentralpublish.MavenCentralPublishExtension;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.publish.PublishingExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigurePublishFeatureTest extends BasePluginTest {
  @BeforeEach
  void applyBasePluginToTheRootProject() {
    // com.stano.java (applied transitively by com.stano.java-library) requires com.stano.base to
    // be applied to the root project; BasePluginTest only applies the base extension feature.
    rootProject.getPluginManager().apply("com.stano.base");
  }

  @Test
  void
      shouldCreateAProjectNamedPublicationWhenStanoMavenUrlIsConfiguredAndMavenCentralPublishIsAbsent() {
    childProject.getPluginManager().apply("com.stano.java-library");
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");

    ((ProjectInternal) childProject).evaluate();

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertTrue(
        publishingExtension.getPublications().stream()
            .anyMatch(publication -> publication.getName().equals(childProject.getName())));
  }

  @Test
  void shouldNotCreateAProjectNamedPublicationWhenMavenCentralPublishIsAlsoApplied() {
    childProject.getPluginManager().apply("com.stano.java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");
    childProject
        .getExtensions()
        .getExtraProperties()
        .set("com.stano.maven.url", "https://maven.example.com/repository");
    configureMavenCentralPublishExtension(childProject);

    ((ProjectInternal) childProject).evaluate();

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    assertFalse(
        publishingExtension.getPublications().stream()
            .anyMatch(publication -> publication.getName().equals(childProject.getName())));
    assertTrue(
        publishingExtension.getPublications().stream()
            .anyMatch(publication -> publication.getName().equals("mavenJava")));
  }

  private void configureMavenCentralPublishExtension(Project project) {
    MavenCentralPublishExtension extension =
        project.getExtensions().getByType(MavenCentralPublishExtension.class);
    extension.setComponentName("java");
    extension.setPomName("Test Library");
    extension.setPomDescription("A test library.");
    extension.setPomUrl("https://github.com/jstano/test-library");
    extension.setLicenseName("Apache License, Version 2.0");
    extension.setLicenseUrl("https://www.apache.org/licenses/LICENSE-2.0");
    extension.setDeveloperId("jstano");
    extension.setDeveloperName("Jeff Stano");
    extension.setDeveloperEmail("jeff@stano.com");
    extension.setScmConnection("scm:git:https://github.com/jstano/test-library.git");
    extension.setScmDeveloperConnection("scm:git:ssh://git@github.com:jstano/test-library.git");
    extension.setScmUrl("https://github.com/jstano/test-library");
  }
}
