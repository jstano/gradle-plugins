package com.stano.gradle.javaconventions.features;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stano.gradle.base.BaseExtension;
import com.stano.gradle.javaconventions.JavaVersionProvider;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class ConfigureCompilersFeatureTest {
  @Test
  void shouldThrowGradleExceptionIfJavaVersionNotCompatible() {
    JavaVersionProvider javaVersionProvider = mock(JavaVersionProvider.class);
    when(javaVersionProvider.currentVersion()).thenReturn(JavaVersion.VERSION_1_8);
    BaseExtension baseExtension = mock(BaseExtension.class);
    when(baseExtension.getJavaVersion()).thenReturn("21");
    ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
    when(extensionContainer.getByType(BaseExtension.class)).thenReturn(baseExtension);
    Project rootProject = mock(Project.class);
    when(rootProject.getExtensions()).thenReturn(extensionContainer);
    Project project = mock(Project.class);
    when(project.getRootProject()).thenReturn(rootProject);
    ConfigureCompilersFeature configureCompilersFeature = new ConfigureCompilersFeature();
    assertThrows(GradleException.class, () -> configureCompilersFeature.apply(project));
  }
}
