package com.stano.gradle.sonar.features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.sonarqube.gradle.ActionBroadcast;
import org.sonarqube.gradle.SonarExtension;
import org.sonarqube.gradle.SonarProperties;

class ConfigureGeneratedSourcesFeatureTest {
  @Test
  void shouldAppendGeneratedSourcesDirectoryToSonarSources() throws Exception {
    var broadcast = new ActionBroadcast<SonarProperties>();
    var sonarExt = new SonarExtension(broadcast);

    var project = ProjectBuilder.builder().build();
    project.getExtensions().add(SonarExtension.SONAR_EXTENSION_NAME, sonarExt);
    project.getPluginManager().apply(JavaPlugin.class);

    // Simulate the generated sources directory existing (created by annotation processing)
    var generatedDir =
        project
            .getLayout()
            .getBuildDirectory()
            .dir("generated/sources/annotationProcessor/java/main")
            .get()
            .getAsFile();
    generatedDir.mkdirs();

    new ConfigureGeneratedSourcesFeature().apply(project);

    // Simulate what SonarPropertyComputer does: pre-populate sonar.sources as a LinkedHashSet
    var existingSourceDir = new File(project.getProjectDir(), "src/main/java");
    var existingSources = new LinkedHashSet<>();
    existingSources.add(existingSourceDir);

    var map = new HashMap<String, Object>();
    map.put("sonar.sources", existingSources);
    broadcast.execute(new SonarProperties(map));

    @SuppressWarnings("unchecked")
    var sources = (LinkedHashSet<Object>) map.get("sonar.sources");
    assertTrue(
        sources.contains(generatedDir),
        "Generated sources dir should be appended to sonar.sources");
  }
}
