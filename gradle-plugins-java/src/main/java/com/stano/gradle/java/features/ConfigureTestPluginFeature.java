package com.stano.gradle.java.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;

public class ConfigureTestPluginFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);

    Task jacocoTestReport = project.getTasks().findByName("jacocoTestReport");
    if (jacocoTestReport != null) {
      project.getTasks().getByName("test").finalizedBy(jacocoTestReport);
    }

    project
        .getTasks()
        .withType(Test.class)
        .configureEach(
            testTask -> {
              testTask.setMinHeapSize("512m");
              testTask.setMaxHeapSize("4096m");
              testTask.jvmArgs(
                  "--add-opens",
                  "java.base/java.lang=ALL-UNNAMED",
                  "-Dhttp.agent=wtf",
                  "-Xshare:off");
              testTask.useJUnitPlatform();

              testTask.doFirst(
                  task -> {
                    var agentJar =
                        testTask.getClasspath().getFiles().stream()
                            .filter(f -> f.getName().startsWith("mockito-core"))
                            .findFirst()
                            .orElseThrow(
                                () ->
                                    new GradleException(
                                        "mockito-core not found on test classpath"));
                    testTask.jvmArgs("-javaagent:" + agentJar.getAbsolutePath());
                    testTask.systemProperty(
                        "pact.provider.branch", baseExtension.getBranchNameProvider().toString());
                  });

              testTask.addTestListener(
                  new TestListener() {
                    @Override
                    public void beforeSuite(TestDescriptor suite) {}

                    @Override
                    public void afterSuite(TestDescriptor suite, TestResult result) {
                      if (suite.getParent() == null) {
                        long totalMs = result.getEndTime() - result.getStartTime();
                        testTask
                            .getLogger()
                            .lifecycle(
                                "Test summary: {} ({} tests, {} passed, {} failed, {} skipped) in"
                                    + " {} ms",
                                result.getResultType(),
                                result.getTestCount(),
                                result.getSuccessfulTestCount(),
                                result.getFailedTestCount(),
                                result.getSkippedTestCount(),
                                totalMs);
                      }
                    }

                    @Override
                    public void beforeTest(TestDescriptor testDescriptor) {}

                    @Override
                    public void afterTest(TestDescriptor testDescriptor, TestResult result) {
                      var key = testDescriptor.getClassName() + "::" + testDescriptor.getName();
                      long durationMs = result.getEndTime() - result.getStartTime();
                      testTask
                          .getLogger()
                          .info("TEST {}: {} ({} ms)", result.getResultType(), key, durationMs);
                    }
                  });

              testTask.systemProperty("pactBrokerUrl", project.findProperty("pactBrokerUrl"));
              testTask.systemProperty(
                  "pactBrokerUsername", project.findProperty("pactBrokerUsername"));
              testTask.systemProperty(
                  "pactBrokerPassword", project.findProperty("pactBrokerPassword"));
              testTask.systemProperty("pact.provider.version", project.getVersion());
              testTask.testLogging(
                  testLoggingContainer -> {
                    testLoggingContainer.events("failed");
                    testLoggingContainer.setExceptionFormat(
                        org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL);
                  });
            });
  }
}
