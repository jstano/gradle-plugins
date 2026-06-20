package com.stano.gradle.sonar.features;

import com.stano.gradle.base.PluginFeature;
import java.util.Collection;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.sonarqube.gradle.SonarExtension;

public class ConfigureGeneratedSourcesFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project.allprojects(
        subproject -> {
          var sonarExt = subproject.getExtensions().findByType(SonarExtension.class);
          if (sonarExt == null) {
            return;
          }
          sonarExt.properties(
              props -> {
                if (!subproject.getPlugins().hasPlugin(JavaPlugin.class)) {
                  return;
                }
                var generatedDir =
                    subproject
                        .getLayout()
                        .getBuildDirectory()
                        .dir("generated/sources/annotationProcessor/java/main")
                        .get()
                        .getAsFile();
                if (!generatedDir.exists()) {
                  subproject
                      .getLogger()
                      .warn(
                          "[sonar] Generated sources directory not found for '{}'; annotation"
                              + " processor output will not be included in sonar.sources. Run"
                              + " compileJava before sonarqube.",
                          subproject.getPath());
                  return;
                }
                var existing = props.getProperties().get("sonar.sources");
                if (existing instanceof Collection) {
                  @SuppressWarnings("unchecked")
                  var sources = (Collection<Object>) existing;
                  sources.add(generatedDir);
                } else {
                  subproject
                      .getLogger()
                      .warn(
                          "[sonar] sonar.sources is not a Collection for '{}'; cannot append"
                              + " generated sources directory. This may indicate a SonarQube"
                              + " plugin version incompatibility.",
                          subproject.getPath());
                }
              });
        });
  }
}
