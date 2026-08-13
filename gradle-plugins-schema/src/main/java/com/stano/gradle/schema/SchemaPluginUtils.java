package com.stano.gradle.schema;

import java.io.File;
import org.gradle.api.Project;

public class SchemaPluginUtils {
  private SchemaPluginUtils() {}

  /** Returns a schema.xml file's name with its extension stripped, e.g. "schema". */
  public static String baseName(File schemaFile) {
    String name = schemaFile.getName();
    int dotIndex = name.lastIndexOf('.');
    return dotIndex < 0 ? name : name.substring(0, dotIndex);
  }

  /**
   * Returns true when the given project's runtimeClasspath resolves an artifact with the given
   * group and name (e.g. a schema-diagram-generator/schema-sql-generator dependency the consumer
   * declared themselves). Returns false (rather than failing the build) when runtimeClasspath
   * doesn't exist yet (e.g. no java plugin applied) or fails to resolve.
   */
  public static boolean hasRuntimeDependency(Project project, String group, String name) {
    try {
      return project
          .getConfigurations()
          .getByName("runtimeClasspath")
          .getResolvedConfiguration()
          .getResolvedArtifacts()
          .stream()
          .anyMatch(
              artifact ->
                  group.equals(artifact.getModuleVersion().getId().getModule().getGroup())
                      && name.equals(artifact.getModuleVersion().getId().getModule().getName()));
    } catch (Exception e) {
      return false;
    }
  }
}
