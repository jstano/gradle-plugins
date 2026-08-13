package com.stano.gradle.schema;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.stano.gradle.base.BasePluginTest;
import org.gradle.api.internal.project.ProjectInternal;
import org.junit.jupiter.api.Test;

class SchemaPluginTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheSchemaExtension() {
    childProject.getPluginManager().apply("com.stano.schema");

    assertNotNull(childProject.getExtensions().findByType(SchemaExtension.class));
  }

  @Test
  void applyingThePluginWithoutTheJavaPluginShouldNotRegisterAnyTask() {
    childProject.getPluginManager().apply("com.stano.schema");

    ((ProjectInternal) childProject).evaluate();

    assertNull(childProject.getTasks().findByName("generateSchemaDiagram"));
    assertNull(childProject.getTasks().findByName("generateSql"));
    assertNull(childProject.getTasks().findByName("installSchema"));
  }

  @Test
  void applyingThePluginWithTheJavaPluginButNoGeneratorDependenciesShouldNotRegisterAnyTask() {
    childProject.getPluginManager().apply("java");
    childProject.getPluginManager().apply("com.stano.schema");

    ((ProjectInternal) childProject).evaluate();

    assertNull(childProject.getTasks().findByName("generateSchemaDiagram"));
    assertNull(childProject.getTasks().findByName("generateSql"));
    assertNull(childProject.getTasks().findByName("installSchema"));
  }
}
