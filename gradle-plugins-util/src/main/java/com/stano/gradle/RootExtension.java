package com.stano.gradle;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class RootExtension {
   private String pactBrokerUrl;
   private String pactBrokerUsername;
   private String pactBrokerPassword;
   private String pactBrokerToken;
   private String dockerRegistryHost;
   private String dockerRegistryUsername;
   private String dockerRegistryPassword;
   private String dockerRegistryAwsProfile;
   private String javaPlatformVersion;
   private String javaVersion;
   private boolean useNvm;
   private String defaultNodeVersion;
   private String contextName;
   private boolean useSemVer;
   private String buildNumber;
   private String buildTime;

   private transient RepositoryUrlProvider repositoryUrlProvider;
   private transient RepositoryOrganizationProvider repositoryOrganizationProvider;
   private transient BranchNameProvider branchNameProvider;
   private transient CommitHashProvider commitHashProvider;
   private transient CommitTimeProvider commitTimeProvider;

   public String getPactBrokerUrl() {
      return pactBrokerUrl;
   }

   public void setPactBrokerUrl(String pactBrokerUrl) {
      this.pactBrokerUrl = pactBrokerUrl;
   }

   public String getPactBrokerUsername() {
      return pactBrokerUsername;
   }

   public void setPactBrokerUsername(String pactBrokerUsername) {
      this.pactBrokerUsername = pactBrokerUsername;
   }

   public String getPactBrokerPassword() {
      return pactBrokerPassword;
   }

   public void setPactBrokerPassword(String pactBrokerPassword) {
      this.pactBrokerPassword = pactBrokerPassword;
   }

   public String getPactBrokerToken() {
      return pactBrokerToken;
   }

   public void setPactBrokerToken(String pactBrokerToken) {
      this.pactBrokerToken = pactBrokerToken;
   }

   public String getDockerRegistryHost() {
      return dockerRegistryHost;
   }

   public void setDockerRegistryHost(String dockerRegistryHost) {
      this.dockerRegistryHost = dockerRegistryHost;
   }

   public String getDockerRegistryUsername() {
      return dockerRegistryUsername;
   }

   public void setDockerRegistryUsername(String dockerRegistryUsername) {
      this.dockerRegistryUsername = dockerRegistryUsername;
   }

   public String getDockerRegistryPassword() {
      return dockerRegistryPassword;
   }

   public void setDockerRegistryPassword(String dockerRegistryPassword) {
      this.dockerRegistryPassword = dockerRegistryPassword;
   }

   public String getDockerRegistryAwsProfile() {
      return dockerRegistryAwsProfile;
   }

   public void setDockerRegistryAwsProfile(String dockerRegistryAwsProfile) {
      this.dockerRegistryAwsProfile = dockerRegistryAwsProfile;
   }

   public String getJavaPlatformVersion() {
      return javaPlatformVersion;
   }

   public void setJavaPlatformVersion(String javaPlatformVersion) {
      this.javaPlatformVersion = javaPlatformVersion;
   }

   public String getJavaVersion() {
      return javaVersion;
   }

   public void setJavaVersion(String javaVersion) {
      this.javaVersion = javaVersion;
   }

   public boolean isUseNvm() {
      return useNvm;
   }

   public void setUseNvm(boolean useNvm) {
      this.useNvm = useNvm;
   }

   public String getDefaultNodeVersion() {
      return defaultNodeVersion;
   }

   public void setDefaultNodeVersion(String defaultNodeVersion) {
      this.defaultNodeVersion = defaultNodeVersion;
   }

   public String getContextName() {
      return contextName;
   }

   public void setContextName(String contextName) {
      this.contextName = contextName;
   }

   public boolean isUseSemVer() {
      return useSemVer;
   }

   public void setUseSemVer(boolean useSemVer) {
      this.useSemVer = useSemVer;
   }

   public LocalDateTime getBuildTime() {
      return LocalDateTime.parse(buildTime);
   }

   public void setBuildTime(LocalDateTime buildTime) {
      this.buildTime = buildTime.toString();
   }

   public String getBuildTimeFormatted() {
      return LocalDateTime.parse(buildTime).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
   }

   public String getBuildNumber() {
      return buildNumber;
   }

   public void setBuildNumber(String buildNumber) {
      this.buildNumber = buildNumber;
   }

   public RepositoryUrlProvider getRepositoryUrlProvider() {
      return repositoryUrlProvider;
   }

   public void setRepositoryUrlProvider(RepositoryUrlProvider repositoryUrlProvider) {
      this.repositoryUrlProvider = repositoryUrlProvider;
   }

   public RepositoryOrganizationProvider getRepositoryOrganizationProvider() {
      return repositoryOrganizationProvider;
   }

   public void setRepositoryOrganizationProvider(RepositoryOrganizationProvider repositoryOrganizationProvider) {
      this.repositoryOrganizationProvider = repositoryOrganizationProvider;
   }

   public BranchNameProvider getBranchNameProvider() {
      return branchNameProvider;
   }

   public void setBranchNameProvider(BranchNameProvider branchNameProvider) {
      this.branchNameProvider = branchNameProvider;
   }

   public CommitHashProvider getCommitHashProvider() {
      return commitHashProvider;
   }

   public void setCommitHashProvider(CommitHashProvider commitHashProvider) {
      this.commitHashProvider = commitHashProvider;
   }

   public CommitTimeProvider getCommitTimeProvider() {
      return commitTimeProvider;
   }

   public void setCommitTimeProvider(CommitTimeProvider commitTimeProvider) {
      this.commitTimeProvider = commitTimeProvider;
   }
}
