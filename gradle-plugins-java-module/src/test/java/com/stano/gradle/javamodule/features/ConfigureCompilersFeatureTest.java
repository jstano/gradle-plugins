package com.stano.gradle.javamodule.features;

import com.stano.gradle.JavaVersionProvider;
import com.stano.gradle.RootExtension;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled
class ConfigureCompilersFeatureTest {
    @Test
    void shouldThrowGradleExceptionIfJavaVersionNotCompatible() {
        JavaVersionProvider javaVersionProvider = mock(JavaVersionProvider.class);
        when(javaVersionProvider.currentVersion()).thenReturn(JavaVersion.VERSION_1_8);

        RootExtension rootExtension = mock(RootExtension.class);
        when(rootExtension.getJavaVersion()).thenReturn("21");

        ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
        when(extensionContainer.getByType(RootExtension.class)).thenReturn(rootExtension);

        Project rootProject = mock(Project.class);
        when(rootProject.getExtensions()).thenReturn(extensionContainer);

        Project project = mock(Project.class);
        when(project.getRootProject()).thenReturn(rootProject);

        ConfigureCompilersFeature configureCompilersFeature = new ConfigureCompilersFeature();

        assertThrows(GradleException.class, () -> configureCompilersFeature.apply(project));
    }
}
