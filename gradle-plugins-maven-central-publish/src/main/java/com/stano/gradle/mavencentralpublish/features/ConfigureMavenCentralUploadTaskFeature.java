package com.stano.gradle.mavencentralpublish.features;

import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.mavencentralpublish.MavenCentralPublishExtension;
import com.stano.gradle.mavencentralpublish.tasks.PublishToMavenCentralTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Zip;

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
              var zipTask =
                  project
                      .getTasks()
                      .named(ConfigureMavenCentralStagingZipFeature.TASK_NAME, Zip.class);
              task.getStagingZip().set(zipTask.flatMap(Zip::getArchiveFile));
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
