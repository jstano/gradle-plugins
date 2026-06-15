package com.stano.gradle.java.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

public class ConfigureTestPluginFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    Test testTask = (Test) project.getTasks().getByName("test");
    testTask.setMinHeapSize("512m");
    testTask.setMaxHeapSize("4096m");
    testTask.jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Dhttp.agent=wtf", "-Xshare:off");
    testTask.useJUnitPlatform();
    var jacocoTestReport = project.getTasks().findByName("jacocoTestReport");
    if (jacocoTestReport != null) {
      testTask.finalizedBy(jacocoTestReport);
    }
    testTask.systemProperty("pactBrokerUrl", project.findProperty("pactBrokerUrl"));
    testTask.systemProperty("pactBrokerUsername", project.findProperty("pactBrokerUsername"));
    testTask.systemProperty("pactBrokerPassword", project.findProperty("pactBrokerPassword"));
    testTask.systemProperty("pact.provider.version", project.getVersion().toString());
    testTask.systemProperty(
        "pact.provider.branch", baseExtension.getBranchNameProvider().toString());
    testTask.testLogging(
        testLoggingContainer -> {
          testLoggingContainer.events("failed");
          testLoggingContainer.setExceptionFormat(
              org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL);
        });
  }
}
