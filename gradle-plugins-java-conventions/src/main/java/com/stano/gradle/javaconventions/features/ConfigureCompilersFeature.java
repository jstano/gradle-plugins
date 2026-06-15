package com.stano.gradle.javaconventions.features;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.base.PluginFeature;
import com.stano.gradle.javaconventions.CompilerUtils;
import com.stano.gradle.javaconventions.JavaVersionProvider;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

public class ConfigureCompilersFeature implements PluginFeature {
  @Override
  public void apply(Project project) {
    BaseExtension baseExtension =
        project.getRootProject().getExtensions().getByType(BaseExtension.class);
    JavaVersionProvider javaVersionProvider = new JavaVersionProvider();
    JavaVersion currentJavaVersion = javaVersionProvider.currentVersion();
    JavaVersion projectJavaVersion = JavaVersion.toVersion(baseExtension.getJavaVersion());
    if (!currentJavaVersion.isCompatibleWith(JavaVersion.toVersion(projectJavaVersion))) {
      throw new GradleException(
          String.format(
              "The current java version %s is not compatible with the project java version %s",
              currentJavaVersion, JavaVersion.toVersion(projectJavaVersion)));
    }
    project
        .getExtensions()
        .configure(
            org.gradle.api.plugins.JavaPluginExtension.class,
            javaPluginExtension -> {
              javaPluginExtension
                  .getToolchain()
                  .getLanguageVersion()
                  .set(JavaLanguageVersion.of(projectJavaVersion.getMajorVersion()));
            });
    new CompilerUtils().configureJavaCompiler(project);
    new CompilerUtils().configureKotlinCompiler(project);
  }
}
