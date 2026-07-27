package com.stano.gradle.mavencentralpublish;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralPomFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralStagingZipFeature;
import com.stano.gradle.mavencentralpublish.features.ConfigureMavenCentralUploadTaskFeature;
import java.util.Set;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.plugins.signing.SigningPlugin;
import org.junit.jupiter.api.Test;

class MavenCentralPublishPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheMavenCentralPublishExtension() {
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    assertNotNull(childProject.getExtensions().findByType(MavenCentralPublishExtension.class));
  }

  @Test
  void applyingThePluginShouldApplyTheMavenPublishPlugin() {
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    assertTrue(childProject.getPlugins().hasPlugin(MavenPublishPlugin.class));
  }

  @Test
  void applyingThePluginShouldApplyTheSigningPlugin() {
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    assertTrue(childProject.getPlugins().hasPlugin(SigningPlugin.class));
  }

  @Test
  void applyingThePluginShouldRegisterTheZipStagingDeployTask() {
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    assertNotNull(
        childProject.getTasks().findByName(ConfigureMavenCentralStagingZipFeature.TASK_NAME));
  }

  @Test
  void applyingThePluginShouldRegisterThePublishToMavenCentralTask() {
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    assertNotNull(
        childProject.getTasks().findByName(ConfigureMavenCentralUploadTaskFeature.TASK_NAME));
  }

  @Test
  void configuringTheExtensionAndEvaluatingShouldCreateAPomCompletePublication() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");
    configureExtension(childProject.getExtensions().getByType(MavenCentralPublishExtension.class));

    ((ProjectInternal) childProject).evaluate();

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    MavenPublication publication =
        (MavenPublication) publishingExtension.getPublications().getByName("mavenJava");

    assertNotNull(publication);
  }

  @Test
  void evaluatingWithoutSettingARequiredExtensionValueShouldFailWithAClearMessage() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");

    Exception exception =
        assertThrows(Exception.class, () -> ((ProjectInternal) childProject).evaluate());
    assertTrue(containsMessage(exception, "mavenCentralPublish.componentName must be set"));
  }

  @Test
  void zipStagingDeployTaskShouldDependOnThePublishToStagingRepositoryTask() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");
    configureExtension(childProject.getExtensions().getByType(MavenCentralPublishExtension.class));

    ((ProjectInternal) childProject).evaluate();

    Task zipTask =
        childProject.getTasks().getByName(ConfigureMavenCentralStagingZipFeature.TASK_NAME);
    Set<? extends Task> dependencies = zipTask.getTaskDependencies().getDependencies(zipTask);

    assertTrue(
        dependencies.stream()
            .filter(PublishToMavenRepository.class::isInstance)
            .map(PublishToMavenRepository.class::cast)
            .anyMatch(
                t ->
                    ConfigureMavenCentralPomFeature.STAGING_REPOSITORY_NAME.equals(
                        t.getRepository().getName())));
  }

  @Test
  void zipStagingDeployTaskShouldNotDependOnOtherPublicationsPublishedToTheStagingRepository() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");
    configureExtension(childProject.getExtensions().getByType(MavenCentralPublishExtension.class));

    PublishingExtension publishingExtension =
        childProject.getExtensions().getByType(PublishingExtension.class);
    publishingExtension.publications(
        publications ->
            publications.create(
                "otherPublication",
                MavenPublication.class,
                publication -> publication.from(childProject.getComponents().getByName("java"))));

    ((ProjectInternal) childProject).evaluate();

    Task zipTask =
        childProject.getTasks().getByName(ConfigureMavenCentralStagingZipFeature.TASK_NAME);
    Set<? extends Task> dependencies = zipTask.getTaskDependencies().getDependencies(zipTask);

    assertTrue(
        dependencies.stream()
            .filter(PublishToMavenRepository.class::isInstance)
            .map(PublishToMavenRepository.class::cast)
            .noneMatch(
                t ->
                    ConfigureMavenCentralPomFeature.STAGING_REPOSITORY_NAME.equals(
                            t.getRepository().getName())
                        && "otherPublication".equals(t.getPublication().getName())));
  }

  @Test
  void settingABlankRequiredExtensionValueShouldFailWithAClearMessage() {
    childProject.getPluginManager().apply("java-library");
    childProject.getPluginManager().apply("com.stano.maven-central-publish");
    MavenCentralPublishExtension extension =
        childProject.getExtensions().getByType(MavenCentralPublishExtension.class);
    configureExtension(extension);
    extension.setPomUrl(""); // blank, not null

    Exception exception =
        assertThrows(Exception.class, () -> ((ProjectInternal) childProject).evaluate());
    assertTrue(containsMessage(exception, "mavenCentralPublish.pomUrl must be set"));
  }

  private boolean containsMessage(Throwable throwable, String expectedMessage) {
    Throwable current = throwable;
    while (current != null) {
      if (current.getMessage() != null && current.getMessage().contains(expectedMessage)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private void configureExtension(MavenCentralPublishExtension extension) {
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
