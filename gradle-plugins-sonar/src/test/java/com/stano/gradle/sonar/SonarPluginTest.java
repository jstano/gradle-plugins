package com.stano.gradle.sonar;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.sonarqube.gradle.SonarExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SonarPluginTest {
    @Test
    void shouldNotApplyIfSonarDisabledSettingIsTrue() {
        var project = ProjectBuilder.builder().build();
        project.getExtensions().getExtraProperties().set("com.stano.sonar.disabled", "true");

        project.getPluginManager().apply("com.stano.sonar");

        assertFalse(project.getPluginManager().hasPlugin("org.sonarqube"));
    }

    @Test
    void shouldNotApplyIfSonarHostSettingIsNotSet() {
        var project = ProjectBuilder.builder().build();

        project.getPluginManager().apply("com.stano.sonar");

        assertFalse(project.getPluginManager().hasPlugin("org.sonarqube"));
    }

    @Test
    void shouldNotApplyIfSonarTokenSettingIsNotSet() {
        var project = ProjectBuilder.builder().build();
        project.getExtensions().getExtraProperties().set("com.stano.sonar.host", "http://localhost:9000");

        project.getPluginManager().apply("com.stano.sonar");

        assertFalse(project.getPluginManager().hasPlugin("org.sonarqube"));
    }

    @Test
    void shouldApplyIfSettingsAreValid() {
        var project = ProjectBuilder.builder().build();
        project.getExtensions().getExtraProperties().set("com.stano.sonar.host", "http://localhost:9000");
        project.getExtensions().getExtraProperties().set("com.stano.sonar.token", "sonar");

        project.getPluginManager().apply("com.stano.sonar");
        project.getExtensions().getByType(SonarExtension.class);

        assertTrue(project.getPluginManager().hasPlugin("org.sonarqube"));
    }
}
