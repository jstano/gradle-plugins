package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.mavencentralpublish.MavenCentralPublishExtension;
import com.stano.gradle.mavencentralpublish.tasks.PublishToMavenCentralTask;
import org.gradle.api.Project;

public class ConfigureMavenCentralUploadTaskFeature implements PluginFeature {
  public static final String TASK_NAME = "publishToMavenCentral";
  private static final String CENTRAL_TOKEN_ENVIRONMENT = "MAVEN_TOKEN";

  @Override
  public void apply(Project project) {
    project
        .getTasks()
        .register(
            TASK_NAME,
            PublishToMavenCentralTask.class,
            task -> {
              task.setGroup("publishing");
              task.dependsOn(ConfigureMavenCentralStagingZipFeature.TASK_NAME);
              task.getStagingZip()
                  .set(project.getLayout().getBuildDirectory().file("tmp/staging-deploy.zip"));
              task.getUploadName()
                  .set(
                      project.provider(
                          () ->
                              String.valueOf(project.getGroup())
                                  + ":"
                                  + project.getName()
                                  + ":"
                                  + project.getVersion()));
              task.getCentralToken().set(project.provider(() -> resolveToken(project)));
            });
  }

  private String resolveToken(Project project) {
    MavenCentralPublishExtension extension =
        project.getExtensions().getByType(MavenCentralPublishExtension.class);
    var properties = project.getExtensions().getExtraProperties().getProperties();
    String propertyName = extension.getCentralTokenPropertyName();
    if (properties.containsKey(propertyName)) {
      return properties.get(propertyName).toString();
    }
    return System.getenv(CENTRAL_TOKEN_ENVIRONMENT);
  }
}
