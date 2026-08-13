package com.stano.gradle.schema.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.stano.gradle.base.BasePluginTest;
import java.io.IOException;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.JavaExec;
import org.junit.jupiter.api.Test;

class ConfigureGenerateSchemaDiagramTaskFeatureTest extends BasePluginTest {
  @Test
  void withoutTheGeneratorDependencyTheTaskShouldNotBeRegistered() {
    childProject.getPluginManager().apply("java");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureGenerateSchemaDiagramTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    assertNull(childProject.getTasks().findByName("generateSchemaDiagram"));
  }

  @Test
  void withTheGeneratorDependencyDeclaredTheTaskShouldBeRegistered() throws IOException {
    SchemaTestSupport.declareLocallyResolvableDependency(childProject, "schema-diagram-generator");
    new SchemaExtensionFeature().apply(childProject);
    new ConfigureGenerateSchemaDiagramTaskFeature().apply(childProject);

    ((ProjectInternal) childProject).evaluate();

    JavaExec task = (JavaExec) childProject.getTasks().getByName("generateSchemaDiagram");
    assertNotNull(task);
    assertEquals("com.stano.schema.gendiagram.GenDiagram", task.getMainClass().get());
    assertEquals("documentation", task.getGroup());
  }
}
