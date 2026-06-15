package com.stano.gradle.base.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.BranchNameProvider;
import com.stano.gradle.base.CommitHashProvider;
import com.stano.gradle.base.CommitTimeProvider;
import com.stano.gradle.base.GradlePluginUtil;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.base.RepositoryOrganizationProvider;
import com.stano.gradle.base.RepositoryUrlProvider;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Project;

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
public class BaseExtensionFeature implements PluginFeature {
  private static final String BASE_EXTENSION_NAME = "root";
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
    if (DefaultGroovyMethods.asBoolean(project.getExtensions().findByName(BASE_EXTENSION_NAME))) {
      return;
    }
    BaseExtension baseExtension = new BaseExtension();
    baseExtension.setMspVersion(getMspVersion(project));
    baseExtension.setPactBrokerUrl(getPactBrokerUrl(project));
    baseExtension.setPactBrokerUsername(getPactBrokerUsername(project));
    baseExtension.setPactBrokerPassword(getPactBrokerPassword(project));
    baseExtension.setPactBrokerToken(getPactBrokerToken(project));
    baseExtension.setDockerRegistryHost(getDockerRegistryHost(project));
    baseExtension.setDockerRegistryUsername(getDockerRegistryUsername(project));
    baseExtension.setDockerRegistryPassword(getDockerRegistryPassword(project));
    baseExtension.setDockerRegistryAwsProfile(getDockerRegistryAwsProfile(project));
    baseExtension.setJavaVersion(getJavaVersion(project));
    //      baseExtension.setUseNvm(getUseNvm(project));
    //      baseExtension.setDefaultNodeVersion(getDefaultNodeVersion(project));
    baseExtension.setContextName(getContextName(project));
    baseExtension.setBuildNumber(getBuildNumber(project));
    baseExtension.setBuildTime(LocalDateTime.now(ZoneId.of("America/Chicago")));
    baseExtension.setRepositoryUrlProvider(new RepositoryUrlProvider(project));
    baseExtension.setRepositoryOrganizationProvider(new RepositoryOrganizationProvider(project));
    baseExtension.setBranchNameProvider(new BranchNameProvider(project));
    baseExtension.setCommitHashProvider(new CommitHashProvider(project));
    baseExtension.setCommitTimeProvider(new CommitTimeProvider(project));
    project.getExtensions().add(BASE_EXTENSION_NAME, baseExtension);
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
