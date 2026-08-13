package com.stano.gradle.schema.features;

import java.io.File;
import java.io.IOException;
import org.gradle.api.Project;

/**
 * Declares a dependency that resolves locally (via a flatDir repository pointed at an empty jar) so
 * tests can exercise the "generator dependency is present" branch of the schema plugin's feature
 * classes without any network access.
 */
final class SchemaTestSupport {
  private SchemaTestSupport() {}

  static void declareLocallyResolvableDependency(Project project, String artifactName)
      throws IOException {
    project.getPluginManager().apply("java");
    File libsDir = new File(project.getProjectDir(), "test-libs");
    libsDir.mkdirs();
    File jarFile = new File(libsDir, artifactName + "-1.0.jar");
    jarFile.createNewFile();
    project.getRepositories().flatDir(repo -> repo.dir(libsDir));
    project.getDependencies().add("implementation", "com.stano:" + artifactName + ":1.0");
  }
}
