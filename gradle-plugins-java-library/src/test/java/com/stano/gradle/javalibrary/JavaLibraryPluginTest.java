package com.stano.gradle.javalibrary;

import com.stano.gradle.plugin.test.BasePluginTest;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class JavaLibraryPluginTest extends BasePluginTest {
    @BeforeEach
    void setup() {
        System.setProperty("com.stano.maven.url", "https://maven.stano.com");
        System.setProperty("com.stano.maven.username", "abc123");
        System.setProperty("com.stano.maven.password", "xyz987");
        System.setProperty("com.stano.sonar.host.url", "http://localhost:9000");
        System.setProperty("com.stano.sonar.login", "sonar");

        childProject.getPluginManager().apply("stano-java-library");
    }

    @Test
    void shouldVerifyThatCommonPluginsAreApplied() {
        assertTrue(childProject.getPluginManager().hasPlugin("stano-java-module"));
        assertTrue(childProject.getPluginManager().hasPlugin("java-library"));
        assertTrue(childProject.getPluginManager().hasPlugin("jacoco"));
        assertTrue(childProject.getPluginManager().hasPlugin("maven-publish"));
    }

    @Test
    void shouldVerifyThatArtifactsAreConfiguredProperly() {
        Jar jarTask = (Jar) childProject.getTasks().findByName("jar");
        Jar sourcesJarTask = (Jar) childProject.getTasks().findByName("sourcesJar");
        Jar javadocJarTask = (Jar) childProject.getTasks().findByName("javadocJar");

        assertTrue(hasExcludes(jarTask.getRootSpec()));
        assertTrue(hasExcludes(sourcesJarTask.getRootSpec()));
        assertTrue(hasExcludes(javadocJarTask.getRootSpec()));

        assertEquals(1, childProject.getConfigurations().getByName("archives").getArtifacts().size());
        assertEquals("test-1.2.3.jar", childProject.getConfigurations().getByName("archives").getArtifacts().getFiles().getSingleFile().getName());

        assertEquals(1, childProject.getConfigurations().getByName("sourcesElements").getArtifacts().size());
        assertEquals("test-1.2.3-sources.jar", childProject.getConfigurations().getByName("sourcesElements").getArtifacts().getFiles().getSingleFile().getName());

        assertEquals(1, childProject.getConfigurations().getByName("javadocElements").getArtifacts().size());
        assertEquals("test-1.2.3-javadoc.jar", childProject.getConfigurations().getByName("javadocElements").getArtifacts().getFiles().getSingleFile().getName());
    }

    @Test
    void shouldVerifyThatPublishTaskIsConfiguredProperly() {
        var pubExt = childProject.getExtensions().getByType(org.gradle.api.publish.PublishingExtension.class);
        assertTrue(pubExt.getPublications().size() >= 1);
        assertTrue(pubExt.getRepositories().size() >= 1);
    }

    private boolean hasExcludes(CopySpecInternal spec) {
        for (var child : spec.getChildren()) {
            if (child.getExcludes().contains("**/.gitkeep")) {
                return true;
            }

            if (hasExcludes(child)) {
                return true;
            }
        }
        return false;
    }
}
