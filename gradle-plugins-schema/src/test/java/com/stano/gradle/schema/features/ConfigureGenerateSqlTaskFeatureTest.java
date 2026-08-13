package com.stano.gradle.schema.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.schema.SchemaExtension;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.JavaExec;
import org.junit.jupiter.api.Test;

class ConfigureGenerateSqlTaskFeatureTest extends BasePluginTest {
  @Test
  void withoutTheGeneratorDependencyTheTaskShouldNotBeRegistered() {
    childProject.getPluginManager().apply("java");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureGenerateSqlTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    assertNull(childProject.getTasks().findByName("generateSql"));
  }

  @Test
  void withTheGeneratorDependencyDeclaredTheTaskShouldBeRegisteredWithDefaultArgs()
      throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-sql-generator");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureGenerateSqlTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("generateSql");
    assertNotNull(task);
    assertEquals("com.stano.schema.gensql.GenSQL", task.getMainClass().get());
    assertEquals("database", task.getGroup());
    List<String> args = task.getArgs();
    assertEquals("POSTGRESQL", args.get(0));
    assertTrue(args.get(1).endsWith("schema.xml"));
    assertEquals(2, args.size());
  }

  @Test
  void optionalExtensionPropertiesShouldBeAppendedAsFlagsWhenSet() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-sql-generator");
    new SchemaExtensionFeature().apply(childProject);
    SchemaExtension extension = childProject.getExtensions().getByType(SchemaExtension.class);
    extension.getDatabaseTypes().set(Set.of("H2", "POSTGRESQL"));
    extension.getForeignKeyMode().set("RELATIONS");
    extension.getBooleanMode().set("NATIVE");
    extension.getOutputMode().set("TRIGGERS_ONLY");
    extension.getPostgresqlVersion().set(15);
    new ConfigureGenerateSqlTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("generateSql");
    List<String> args = task.getArgs();
    assertTrue(args.contains("--foreign-key-mode=RELATIONS"));
    assertTrue(args.contains("--boolean-mode=NATIVE"));
    assertTrue(args.contains("--output-triggers-only"));
    assertTrue(args.contains("--postgresql-version=15"));
    assertEquals(2, task.getOutputs().getFiles().getFiles().size());
  }
}
