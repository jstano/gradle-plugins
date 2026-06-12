package com.stano.gradle;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Project;

import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * Gitlab (https://docs.gitlab.com/ci/variables/predefined_variables/)
 * -------------------------------------------------------------------
 * CI = true
 * CI_COMMIT_AUTHOR
 * CI_COMMIT_BRANCH
 * CI_COMMIT_DESCRIPTION
 * CI_COMMIT_MESSAGE
 * CI_COMMIT_REF_NAME
 * CI_COMMIT_SHA
 * CI_COMMIT_SHORT_SHA
 * CI_COMMIT_TIMESTAMP
 */
public class RootExtensionFeature implements PluginFeature {
  private static final String ROOT_EXTENSION_NAME = "root";
  private static final String MSP_VERSION = "mspVersion";
  private static final String PACT_BROKER_URL = "pactBrokerUrl";
  private static final String PACT_BROKER_USERNAME = "pactBrokerUsername";
  private static final String PACT_BROKER_PASSWORD = "pactBrokerPassword";
  private static final String PACT_BROKER_TOKEN = "pactBrokerToken";
  private static final String DOCKER_REGISTRY_HOST = "dockerRegistryHost";
  private static final String DOCKER_REGISTRY_USERNAME = "dockerRegistryUsername";
  private static final String DOCKER_REGISTRY_PASSWORD = "dockerRegistryPassword";
  private static final String DOCKER_REGISTRY_AWS_PROFILE = "dockerRegistryAwsProfile";
  private static final String JAVA_VERSION = "javaVersion";
  private static final String DEFAULT_NODE_VERSION = "com.stano.default-node-version";
  private static final String USE_NVM = "com.stano.use-nvm";
  private static final String CONTEXT_NAME = "contextName";
  private static final String DEFAULT_JAVA_VERSION = "21";

  @Override
  public void apply(Project project) {
    if (DefaultGroovyMethods.asBoolean(project.getExtensions().findByName(ROOT_EXTENSION_NAME))) {
      return;
    }
    RootExtension rootExtension = new RootExtension();
    rootExtension.setMspVersion(getMspVersion(project));
    rootExtension.setPactBrokerUrl(getPactBrokerUrl(project));
    rootExtension.setPactBrokerUsername(getPactBrokerUsername(project));
    rootExtension.setPactBrokerPassword(getPactBrokerPassword(project));
    rootExtension.setPactBrokerToken(getPactBrokerToken(project));
    rootExtension.setDockerRegistryHost(getDockerRegistryHost(project));
    rootExtension.setDockerRegistryUsername(getDockerRegistryUsername(project));
    rootExtension.setDockerRegistryPassword(getDockerRegistryPassword(project));
    rootExtension.setDockerRegistryAwsProfile(getDockerRegistryAwsProfile(project));
    rootExtension.setJavaVersion(getJavaVersion(project));
    //      rootExtension.setUseNvm(getUseNvm(project));
    //      rootExtension.setDefaultNodeVersion(getDefaultNodeVersion(project));
    rootExtension.setContextName(getContextName(project));
    rootExtension.setBuildNumber(getBuildNumber(project));
    rootExtension.setBuildTime(LocalDateTime.now(ZoneId.of("America/Chicago")));
    rootExtension.setRepositoryUrlProvider(new RepositoryUrlProvider(project));
    rootExtension.setRepositoryOrganizationProvider(new RepositoryOrganizationProvider(project));
    rootExtension.setBranchNameProvider(new BranchNameProvider(project));
    rootExtension.setCommitHashProvider(new CommitHashProvider(project));
    rootExtension.setCommitTimeProvider(new CommitTimeProvider(project));
    project.getExtensions().add(ROOT_EXTENSION_NAME, rootExtension);
  }

  private static String getMspVersion(Project project) {
    return getProjectProperty(project, MSP_VERSION);
  }

  private static String getPactBrokerUrl(Project project) {
    return getProjectOrSystemProperty(project, PACT_BROKER_URL);
  }

  private static String getPactBrokerUsername(Project project) {
    return getProjectOrSystemProperty(project, PACT_BROKER_USERNAME);
  }

  private static String getPactBrokerPassword(Project project) {
    return getProjectOrSystemProperty(project, PACT_BROKER_PASSWORD);
  }

  private static String getPactBrokerToken(Project project) {
    return getProjectOrSystemProperty(project, PACT_BROKER_TOKEN);
  }

  private static String getDockerRegistryHost(Project project) {
    return getProjectOrSystemProperty(project, DOCKER_REGISTRY_HOST);
  }

  private static String getDockerRegistryUsername(Project project) {
    return getProjectOrSystemProperty(project, DOCKER_REGISTRY_USERNAME);
  }

  private static String getDockerRegistryPassword(Project project) {
    return getProjectOrSystemProperty(project, DOCKER_REGISTRY_PASSWORD);
  }

  private static String getDockerRegistryAwsProfile(Project project) {
    return getProjectOrSystemProperty(project, DOCKER_REGISTRY_AWS_PROFILE);
  }

  private static String getJavaVersion(Project project) {
    return getProjectOrSystemProperty(project, JAVA_VERSION, DEFAULT_JAVA_VERSION);
  }

  private static boolean getUseNvm(Project project) {
    return Boolean.parseBoolean(getProjectOrSystemProperty(project, USE_NVM, "false"));
  }

  private static String getDefaultNodeVersion(Project project) {
    return getProjectOrSystemProperty(project, DEFAULT_NODE_VERSION, "12");
  }

  private static String getContextName(Project project) {
    return getProjectProperty(project, CONTEXT_NAME);
  }

  private String getBuildNumber(Project project) {
    return getProjectOrSystemProperty(project, "BUILD_NUMBER");
  }

  private static String getProjectProperty(Project project, String propertyName) {
    return getProjectProperty(project, propertyName, null);
  }

  private static String getProjectProperty(
      Project project, String propertyName, String defaultValue) {
    return GradlePluginUtil.getProjectProperty(project, propertyName, defaultValue);
  }

  private static String getProjectOrSystemProperty(Project project, String propertyName) {
    return getProjectOrSystemProperty(project, propertyName, null);
  }

  private static String getProjectOrSystemProperty(
      Project project, String propertyName, String defaultValue) {
    return GradlePluginUtil.getProjectOrSystemProperty(project, propertyName, defaultValue);
  }
}
