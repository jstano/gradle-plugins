package com.stano.gradle.schema.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.schema.SchemaExtension;
import java.io.IOException;
import java.util.List;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.JavaExec;
import org.junit.jupiter.api.Test;

class ConfigureInstallSchemaTaskFeatureTest extends BasePluginTest {
  @Test
  void withoutTheGeneratorDependencyTheTaskShouldNotBeRegistered() {
    childProject.getPluginManager().apply("java");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureInstallSchemaTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    assertNull(childProject.getTasks().findByName("installSchema"));
  }

  @Test
  void withTheGeneratorDependencyDeclaredTheTaskShouldBeRegistered() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-installer-flyway");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureInstallSchemaTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("installSchema");
    assertNotNull(task);
    assertEquals("com.stano.schema.installer.flyway.InstallSchema", task.getMainClass().get());
    assertEquals("database", task.getGroup());
  }

  @Test
  void connectionCredentialsShouldBePassedAsEnvironmentVariablesNotArgs() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-installer-flyway");
    new SchemaExtensionFeature().apply(childProject);
    SchemaExtension extension = childProject.getExtensions().getByType(SchemaExtension.class);
    extension.getSchemaJdbcUrl().set("jdbc:postgresql://localhost/mydb");
    extension.getSchemaJdbcUsername().set("myuser");
    extension.getSchemaJdbcPassword().set("mypassword");
    new ConfigureInstallSchemaTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("installSchema");
    assertEquals("jdbc:postgresql://localhost/mydb", task.getEnvironment().get("SCHEMA_JDBC_URL"));
    assertEquals("myuser", task.getEnvironment().get("SCHEMA_JDBC_USERNAME"));
    assertEquals("mypassword", task.getEnvironment().get("SCHEMA_JDBC_PASSWORD"));
    List<String> args = task.getArgs();
    assertFalse(args.contains("mypassword"));
  }

  @Test
  void withoutCredentialsConfiguredNoEnvironmentVariablesShouldBeSet() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-installer-flyway");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureInstallSchemaTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("installSchema");
    assertFalse(task.getEnvironment().containsKey("SCHEMA_JDBC_URL"));
    assertFalse(task.getEnvironment().containsKey("SCHEMA_JDBC_USERNAME"));
    assertFalse(task.getEnvironment().containsKey("SCHEMA_JDBC_PASSWORD"));
  }

  @Test
  void migrationScriptLocatorShouldBeAppendedAsASecondArgWhenSet() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-installer-flyway");
    new SchemaExtensionFeature().apply(childProject);
    SchemaExtension extension = childProject.getExtensions().getByType(SchemaExtension.class);
    extension.getMigrationScriptLocator().set("db/migration");
    new ConfigureInstallSchemaTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("installSchema");
    List<String> args = task.getArgs();
    assertEquals(2, args.size());
    assertTrue(args.get(0).endsWith("schema.xml"));
    assertEquals("db/migration", args.get(1));
  }
}
