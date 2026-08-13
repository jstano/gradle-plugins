plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.plugin.publish)
}

gradlePlugin {
  website = "https://github.com/jstano/gradle-plugins"
  vcsUrl = "https://github.com/jstano/gradle-plugins"
  plugins {
    create("schema") {
      id = "com.stano.schema"
      implementationClass = "com.stano.gradle.schema.SchemaPlugin"
      displayName = "Schema Plugin"
      description = "Adds generateSchemaDiagram and generateSql tasks that run " +
        "com.stano.schema.gendiagram.GenDiagram and com.stano.schema.gensql.GenSQL against a " +
        "schema.xml file. Each task is only registered when the corresponding " +
        "com.stano:schema-diagram-generator / com.stano:schema-sql-generator dependency is " +
        "present on the project's runtimeClasspath; otherwise a warning is logged and the task " +
        "is skipped."
      tags = listOf("convention", "schema", "sql", "diagram", "java")
    }
  }
}

dependencies {
  implementation(project(":gradle-plugins-base"))

  testImplementation(project(":gradle-plugins-java"))
  testImplementation(testFixtures(project(":gradle-plugins-base")))
}
