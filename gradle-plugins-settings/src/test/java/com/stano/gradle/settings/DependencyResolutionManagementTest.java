package com.stano.gradle.settings;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.HttpHeaderCredentials;
import org.gradle.api.initialization.Settings;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.junit.jupiter.api.Test;

class DependencyResolutionManagementTest {
  private final DependencyResolutionManagement dependencyResolutionManagement =
      new DependencyResolutionManagement();

  @Test
  void configuringWithoutAStanoMavenUrlShouldStillAddMavenLocalAndMavenCentral() {
    var repositoryHandler = mock(RepositoryHandler.class);
    var settings = mockSettings(Map.of(), repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    verify(repositoryHandler).mavenLocal();
    verify(repositoryHandler).mavenCentral();
  }

  @Test
  void configuringWithoutAStanoMavenUrlShouldNotAddTheStanoMavenRepository() {
    var repositoryHandler = mock(RepositoryHandler.class);
    var settings = mockSettings(Map.of(), repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    verify(repositoryHandler, never()).maven(any(Action.class));
  }

  @Test
  void configuringWithAStanoMavenUrlShouldStillAddMavenLocalAndMavenCentral() {
    var repositoryHandler = mock(RepositoryHandler.class);
    var mavenArtifactRepository = stubMavenRepository(repositoryHandler);
    var settings =
        mockSettings(
            Map.of("com.stano.maven.url", "https://maven.example.com/repository"),
            repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    verify(repositoryHandler).mavenLocal();
    verify(repositoryHandler).mavenCentral();
  }

  @Test
  void configuringWithAStanoMavenUrlShouldAddTheStanoMavenRepository() {
    var repositoryHandler = mock(RepositoryHandler.class);
    var mavenArtifactRepository = stubMavenRepository(repositoryHandler);
    var settings =
        mockSettings(
            Map.of("com.stano.maven.url", "https://maven.example.com/repository"),
            repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    verify(mavenArtifactRepository).setName("stano-maven");
    verify(mavenArtifactRepository).setUrl("https://maven.example.com/repository");
  }

  @Test
  void configuringWithAStanoMavenUrlAndATokenShouldConfigureHttpHeaderCredentials() {
    // CI_JOB_TOKEN always wins when present, which would invalidate this test's assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
    var repositoryHandler = mock(RepositoryHandler.class);
    var mavenArtifactRepository = stubMavenRepository(repositoryHandler);
    var headerCredentials = mock(HttpHeaderCredentials.class);
    when(mavenArtifactRepository.getCredentials(HttpHeaderCredentials.class))
        .thenReturn(headerCredentials);
    var settings =
        mockSettings(
            Map.of(
                "com.stano.maven.url", "https://maven.example.com/repository",
                "com.stano.maven.token", "abc123"),
            repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    verify(headerCredentials).setName("Private-Token");
    verify(headerCredentials).setValue("abc123");
  }

  @Test
  void
      configuringWithAStanoMavenUrlAndUsernameAndPasswordButNoTokenShouldConfigurePasswordCredentials() {
    // CI_JOB_TOKEN always wins when present, which would invalidate this test's assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
    var repositoryHandler = mock(RepositoryHandler.class);
    var mavenArtifactRepository = stubMavenRepository(repositoryHandler);
    var settings =
        mockSettings(
            Map.of(
                "com.stano.maven.url", "https://maven.example.com/repository",
                "com.stano.maven.username", "user",
                "com.stano.maven.password", "pass"),
            repositoryHandler);

    dependencyResolutionManagement.configureDependencyResolutionManagement(settings);

    var credentials = mavenArtifactRepository.getCredentials(PasswordCredentials.class);
    verify(credentials).setUsername("user");
    verify(credentials).setPassword("pass");
  }

  private MavenArtifactRepository stubMavenRepository(RepositoryHandler repositoryHandler) {
    var mavenArtifactRepository = mock(MavenArtifactRepository.class);
    var credentials = mock(PasswordCredentials.class);
    when(mavenArtifactRepository.getCredentials(PasswordCredentials.class)).thenReturn(credentials);
    doAnswer(
            invocation -> {
              Action<MavenArtifactRepository> action = invocation.getArgument(0);
              action.execute(mavenArtifactRepository);
              return null;
            })
        .when(repositoryHandler)
        .maven(any(Action.class));
    return mavenArtifactRepository;
  }

  private Settings mockSettings(
      Map<String, ?> extraProperties, RepositoryHandler repositoryHandler) {
    var settings = mock(Settings.class);
    var extensionContainer = mock(ExtensionContainer.class);
    var extraPropertiesExtension = mock(ExtraPropertiesExtension.class);
    when(settings.getExtensions()).thenReturn(extensionContainer);
    when(extensionContainer.getExtraProperties()).thenReturn(extraPropertiesExtension);
    when(extraPropertiesExtension.getProperties()).thenReturn(new HashMap<>(extraProperties));

    var dependencyResolutionManagementExtension =
        mock(org.gradle.api.initialization.resolve.DependencyResolutionManagement.class);
    when(settings.getDependencyResolutionManagement())
        .thenReturn(dependencyResolutionManagementExtension);
    doAnswer(
            invocation -> {
              Action<RepositoryHandler> action = invocation.getArgument(0);
              action.execute(repositoryHandler);
              return null;
            })
        .when(dependencyResolutionManagementExtension)
        .repositories(any());

    return settings;
  }
}
