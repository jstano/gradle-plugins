package com.stano.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildInfoUpdaterTest {
    private Project project;

    @BeforeEach
    void setup() throws IOException {
        File tempDir = Files.createTempDirectory("gradle-project-plugin-test").toFile();
        project = ProjectBuilder.builder()
            .withName("test")
            .withProjectDir(tempDir)
            .build();
        project.setVersion("1.2.3");
        project.getProjectDir().mkdirs();
        new File(project.getBuildDir(), "resources/test").mkdirs();
        new RootExtensionFeature().apply(project);
    }

    @AfterEach
    void cleanup() throws IOException {
        Path tempDir = project.getProjectDir().toPath();
        Files.walk(tempDir)
            .sorted((a, b) -> b.compareTo(a))
            .forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // Ignore
                }
            });
    }

    @Test
    void shouldBeAbleToUpdateApplicationYml() throws IOException {
        Environment environment = mock(Environment.class);
        when(environment.getEnvironmentVariable("BUILD_NUMBER")).thenReturn("123");
        when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("main");
        when(environment.getEnvironmentVariable("JOB_NAME")).thenReturn("job/dev/job/taps/job/main");
        RootExtension rootExtension = mock(RootExtension.class);
        BuildInfoProvider buildInfo = new BuildInfoProvider(environment);

        Files.copy(Path.of("src/test/resources/application.yml"),
            Path.of(project.getBuildDir().getAbsolutePath(), "resources/test/application.yml"));

        String applicationYmlPath = project.getBuildDir().getAbsolutePath() + "/resources/test/application.yml";
        BuildInfoUpdater.updateYmlWithBuildInfo(rootExtension, project.getVersion().toString(), buildInfo, applicationYmlPath);

        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader(applicationYmlPath)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(reader);
            @SuppressWarnings("unchecked")
            Map<String, Object> info = (Map<String, Object>) data.get("info");
            @SuppressWarnings("unchecked")
            Map<String, Object> app = (Map<String, Object>) info.get("app");
            @SuppressWarnings("unchecked")
            Map<String, Object> build = (Map<String, Object>) info.get("build");

            assertEquals("Test App Name", app.get("name"));
            assertEquals("Test App Description", app.get("description"));
            assertEquals(project.getVersion().toString(), app.get("version"));
            assertEquals("123", build.get("number"));
            assertEquals("main", build.get("branch"));
            assertEquals("job/dev/job/taps/job/main", build.get("job"));
        }
    }

    @Test
    void shouldNotWriteIfApplicationYmlDoesNotExist() {
        Environment environment = mock(Environment.class);
        when(environment.getEnvironmentVariable("BUILD_NUMBER")).thenReturn("123");
        when(environment.getEnvironmentVariable("BRANCH_NAME")).thenReturn("main");
        when(environment.getEnvironmentVariable("JOB_NAME")).thenReturn("job/dev/job/taps/job/main");
        RootExtension rootExtension = mock(RootExtension.class);
        BuildInfoProvider buildInfo = new BuildInfoProvider(environment);

        File applicationYmlFile = new File(project.getBuildDir(), "resources/test/application.yml");

        BuildInfoUpdater.updateYmlWithBuildInfo(rootExtension, project.getVersion().toString(), buildInfo,
            applicationYmlFile.getAbsolutePath());

        assertFalse(applicationYmlFile.exists());
    }
}
