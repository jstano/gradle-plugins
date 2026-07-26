package com.stano.gradle.mavencentralpublish;

public class MavenCentralPublishExtension {
  private String componentName;
  private String pomName;
  private String pomDescription;
  private String pomUrl;
  private String licenseName;
  private String licenseUrl;
  private String developerId;
  private String developerName;
  private String developerEmail;
  private String scmConnection;
  private String scmDeveloperConnection;
  private String scmUrl;
  private String centralTokenPropertyName = "com.stano.maven.central.token";

  public String getComponentName() {
    return componentName;
  }

  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  public String getPomName() {
    return pomName;
  }

  public void setPomName(String pomName) {
    this.pomName = pomName;
  }

  public String getPomDescription() {
    return pomDescription;
  }

  public void setPomDescription(String pomDescription) {
    this.pomDescription = pomDescription;
  }

  public String getPomUrl() {
    return pomUrl;
  }

  public void setPomUrl(String pomUrl) {
    this.pomUrl = pomUrl;
  }

  public String getLicenseName() {
    return licenseName;
  }

  public void setLicenseName(String licenseName) {
    this.licenseName = licenseName;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public String getDeveloperId() {
    return developerId;
  }

  public void setDeveloperId(String developerId) {
    this.developerId = developerId;
  }

  public String getDeveloperName() {
    return developerName;
  }

  public void setDeveloperName(String developerName) {
    this.developerName = developerName;
  }

  public String getDeveloperEmail() {
    return developerEmail;
  }

  public void setDeveloperEmail(String developerEmail) {
    this.developerEmail = developerEmail;
  }

  public String getScmConnection() {
    return scmConnection;
  }

  public void setScmConnection(String scmConnection) {
    this.scmConnection = scmConnection;
  }

  public String getScmDeveloperConnection() {
    return scmDeveloperConnection;
  }

  public void setScmDeveloperConnection(String scmDeveloperConnection) {
    this.scmDeveloperConnection = scmDeveloperConnection;
  }

  public String getScmUrl() {
    return scmUrl;
  }

  public void setScmUrl(String scmUrl) {
    this.scmUrl = scmUrl;
  }

  public String getCentralTokenPropertyName() {
    return centralTokenPropertyName;
  }

  public void setCentralTokenPropertyName(String centralTokenPropertyName) {
    this.centralTokenPropertyName = centralTokenPropertyName;
  }
}
