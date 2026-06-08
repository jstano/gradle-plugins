package com.stano.gradle;

import com.stano.gradle.plugin.test.BasePluginTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class GradlePluginsTest extends BasePluginTest {
    @Test
    void verifyThatAllPluginsLoadProperly() {
        rootProject.getPluginManager().apply("stano-project");
        childProject.getPluginManager().apply("stano-java-module");
        childProject.getPluginManager().apply("stano-java-sonar");

        assertTrue(rootProject.getPluginManager().hasPlugin("stano-project"));
        assertTrue(childProject.getPluginManager().hasPlugin("stano-java-module"));
        assertTrue(childProject.getPluginManager().hasPlugin("stano-java-sonar"));
    }
}
