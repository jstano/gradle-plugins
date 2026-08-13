package com.stano.gradle.schema;

import com.stano.gradle.base.GradlePluginUtil;
import java.util.Set;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public class SchemaExtension {
  private final RegularFileProperty schemaFile;
  private final Property<String> diagramFormat;
  private final SetProperty<String> databaseTypes;
  private final Property<String> foreignKeyMode;
  private final Property<String> booleanMode;
  private final Property<String> outputMode;
  private final Property<Integer> postgresqlVersion;
  private final Property<String> migrationScriptLocator;
  private final Property<String> schemaJdbcUrl;
  private final Property<String> schemaJdbcUsername;
  private final Property<String> schemaJdbcPassword;

  @Inject
  public SchemaExtension(Project project) {
    ObjectFactory objectFactory = project.getObjects();

    schemaFile =
        objectFactory
            .fileProperty()
            .convention(
                project.getLayout().getProjectDirectory().file("src/main/resources/db/schema.xml"));
    diagramFormat =
        objectFactory
            .property(String.class)
            .convention(
                GradlePluginUtil.getProjectOrSystemProperty(project, "diagramFormat", "MERMAID")
                    .toUpperCase());
    databaseTypes = objectFactory.setProperty(String.class).convention(Set.of("POSTGRESQL"));
    foreignKeyMode = objectFactory.property(String.class);
    booleanMode = objectFactory.property(String.class);
    outputMode = objectFactory.property(String.class);
    postgresqlVersion = objectFactory.property(Integer.class);
    migrationScriptLocator = objectFactory.property(String.class);
    schemaJdbcUrl = stringPropertyDefaultingFrom(objectFactory, project, "schemaJdbcUrl");
    schemaJdbcUsername = stringPropertyDefaultingFrom(objectFactory, project, "schemaJdbcUsername");
    schemaJdbcPassword = stringPropertyDefaultingFrom(objectFactory, project, "schemaJdbcPassword");
  }

  /**
   * A {@code Property<String>} that defaults to the given project/system property or the matching
   * {@code CONSTANT_CASE} environment variable (e.g. {@code schemaJdbcUrl} -> {@code
   * SCHEMA_JDBC_URL}), via {@link GradlePluginUtil#getProjectOrSystemProperty}, and is otherwise
   * left unset. Used for connection details that shouldn't be hardcoded in a build script.
   */
  private static Property<String> stringPropertyDefaultingFrom(
      ObjectFactory objectFactory, Project project, String propertyName) {
    Property<String> property = objectFactory.property(String.class);
    String defaultValue = GradlePluginUtil.getProjectOrSystemProperty(project, propertyName);
    if (defaultValue != null) {
      property.convention(defaultValue);
    }
    return property;
  }

  public RegularFileProperty getSchemaFile() {
    return schemaFile;
  }

  public Property<String> getDiagramFormat() {
    return diagramFormat;
  }

  public SetProperty<String> getDatabaseTypes() {
    return databaseTypes;
  }

  public Property<String> getForeignKeyMode() {
    return foreignKeyMode;
  }

  public Property<String> getBooleanMode() {
    return booleanMode;
  }

  public Property<String> getOutputMode() {
    return outputMode;
  }

  public Property<Integer> getPostgresqlVersion() {
    return postgresqlVersion;
  }

  public Property<String> getMigrationScriptLocator() {
    return migrationScriptLocator;
  }

  public Property<String> getSchemaJdbcUrl() {
    return schemaJdbcUrl;
  }

  public Property<String> getSchemaJdbcUsername() {
    return schemaJdbcUsername;
  }

  public Property<String> getSchemaJdbcPassword() {
    return schemaJdbcPassword;
  }
}
