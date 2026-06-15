package com.stano.gradle.javalibrary.features;

import com.stano.gradle.base.PluginFeature;
import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.CoreJavadocOptions;

public class ConfigureJavadocFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    project
        .getTasks()
        .withType(
            Javadoc.class,
            javadoc -> {
              ((CoreJavadocOptions) javadoc.getOptions())
                  .addStringOption("Xdoclint:none", "-quiet");
            });
  }
}
