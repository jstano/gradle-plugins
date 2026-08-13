package com.stano.gradle.schema.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.stano.gradle.base.BasePluginTest;
import com.stano.gradle.schema.SchemaExtension;
import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SchemaExtensionFeatureTest extends BasePluginTest {
  @Test
  void applyingThePluginShouldRegisterTheSchemaExtension() {
    new SchemaExtensionFeature().apply(childProject);

    assertNotNull(childProject.getExtensions().findByType(SchemaExtension.class));
  }

  @Test
  void applyingThePluginTwiceShouldBeIdempotent() {
    new SchemaExtensionFeature().apply(childProject);
    SchemaExtension firstExtension = childProject.getExtensions().getByType(SchemaExtension.class);

    new SchemaExtensionFeature().apply(childProject);
    SchemaExtension secondExtension = childProject.getExtensions().getByType(SchemaExtension.class);

    assertSame(firstExtension, secondExtension);
  }

  @Test
  void schemaFileShouldDefaultToSrcMainResourcesDbSchemaXml() {
    new SchemaExtensionFeature().apply(childProject);

    SchemaExtension schemaExtension = childProject.getExtensions().getByType(SchemaExtension.class);
    File expected = new File(childProject.getProjectDir(), "src/main/resources/db/schema.xml");
    assertEquals(expected, schemaExtension.getSchemaFile().get().getAsFile());
  }

  @Test
  void diagramFormatShouldDefaultToMermaid() {
    new SchemaExtensionFeature().apply(childProject);

    SchemaExtension schemaExtension = childProject.getExtensions().getByType(SchemaExtension.class);
    assertEquals("MERMAID", schemaExtension.getDiagramFormat().get());
  }

  @Test
  void databaseTypesShouldDefaultToPostgresql() {
    new SchemaExtensionFeature().apply(childProject);

    SchemaExtension schemaExtension = childProject.getExtensions().getByType(SchemaExtension.class);
    assertEquals(Set.of("POSTGRESQL"), schemaExtension.getDatabaseTypes().get());
  }

  @Test
  void foreignKeyModeBooleanModeOutputModeAndPostgresqlVersionShouldHaveNoDefault() {
    new SchemaExtensionFeature().apply(childProject);

    SchemaExtension schemaExtension = childProject.getExtensions().getByType(SchemaExtension.class);
    assertFalse(schemaExtension.getForeignKeyMode().isPresent());
    assertFalse(schemaExtension.getBooleanMode().isPresent());
    assertFalse(schemaExtension.getOutputMode().isPresent());
    assertFalse(schemaExtension.getPostgresqlVersion().isPresent());
  }
}
