package com.stano.gradle.base;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.AuthenticationContainer;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.HttpHeaderCredentials;
import org.gradle.authentication.http.HttpHeaderAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MavenRepositoryCredentialsTest {
  @BeforeEach
  void assumeNotRunningInGitLabCi() {
    // CI_JOB_TOKEN always wins when present, which would invalidate these tests' assumptions.
    assumeTrue(System.getenv("CI_JOB_TOKEN") == null);
  }

  @Test
  void
      configuringCredentialsWithOnlyATokenConfiguredShouldSetHttpHeaderCredentialsWithTheDefaultHeaderName() {
    var repository = mock(MavenArtifactRepository.class);
    var headerCredentials = mock(HttpHeaderCredentials.class);
    when(repository.getCredentials(HttpHeaderCredentials.class)).thenReturn(headerCredentials);

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.token", "abc123"), repository);

    verify(headerCredentials).setName("Private-Token");
    verify(headerCredentials).setValue("abc123");
  }

  @Test
  void configuringCredentialsWithATokenConfiguredShouldRegisterTheHttpHeaderAuthenticationScheme() {
    var repository = mock(MavenArtifactRepository.class);
    when(repository.getCredentials(HttpHeaderCredentials.class))
        .thenReturn(mock(HttpHeaderCredentials.class));
    var authenticationContainer = stubAuthentication(repository);

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.token", "abc123"), repository);

    verify(authenticationContainer).create("header", HttpHeaderAuthentication.class);
  }

  @Test
  void
      configuringCredentialsWithATokenAndACustomHeaderNameConfiguredShouldUseTheCustomHeaderName() {
    var repository = mock(MavenArtifactRepository.class);
    var headerCredentials = mock(HttpHeaderCredentials.class);
    when(repository.getCredentials(HttpHeaderCredentials.class)).thenReturn(headerCredentials);

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.token", "abc123", "com.stano.maven.token-header", "Job-Token"),
        repository);

    verify(headerCredentials).setName("Job-Token");
    verify(headerCredentials).setValue("abc123");
  }

  @Test
  void configuringCredentialsWithATokenConfiguredShouldNotSetPasswordCredentials() {
    var repository = mock(MavenArtifactRepository.class);
    when(repository.getCredentials(HttpHeaderCredentials.class))
        .thenReturn(mock(HttpHeaderCredentials.class));

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.token", "abc123"), repository);

    verify(repository, never()).getCredentials(PasswordCredentials.class);
  }

  @Test
  void
      configuringCredentialsWithoutATokenButWithUsernameAndPasswordConfiguredShouldSetPasswordCredentials() {
    var repository = mock(MavenArtifactRepository.class);
    var credentials = mock(PasswordCredentials.class);
    when(repository.getCredentials(PasswordCredentials.class)).thenReturn(credentials);

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.username", "user", "com.stano.maven.password", "pass"), repository);

    verify(credentials).setUsername("user");
    verify(credentials).setPassword("pass");
  }

  @Test
  void
      configuringCredentialsWithoutATokenButWithUsernameAndPasswordConfiguredShouldNotRegisterHeaderAuthentication() {
    var repository = mock(MavenArtifactRepository.class);
    when(repository.getCredentials(PasswordCredentials.class))
        .thenReturn(mock(PasswordCredentials.class));

    MavenRepositoryCredentials.configureCredentials(
        Map.of("com.stano.maven.username", "user", "com.stano.maven.password", "pass"), repository);

    verify(repository, never()).authentication(any());
  }

  @Test
  void
      configuringCredentialsWithNeitherATokenNorUsernameAndPasswordConfiguredShouldSetPasswordCredentialsWithNullValues() {
    var repository = mock(MavenArtifactRepository.class);
    var credentials = mock(PasswordCredentials.class);
    when(repository.getCredentials(PasswordCredentials.class)).thenReturn(credentials);

    MavenRepositoryCredentials.configureCredentials(Map.of(), repository);

    verify(credentials).setUsername(null);
    verify(credentials).setPassword(null);
  }

  @SuppressWarnings("unchecked")
  private AuthenticationContainer stubAuthentication(MavenArtifactRepository repository) {
    var authenticationContainer = mock(AuthenticationContainer.class);
    doAnswer(
            invocation -> {
              Action<AuthenticationContainer> action = invocation.getArgument(0);
              action.execute(authenticationContainer);
              return null;
            })
        .when(repository)
        .authentication(any());
    return authenticationContainer;
  }
}
