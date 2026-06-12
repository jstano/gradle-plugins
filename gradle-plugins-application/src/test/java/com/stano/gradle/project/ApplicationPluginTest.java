package com.stano.gradle.project;

import com.stano.gradle.ProjectVersionProvider;
import com.stano.gradle.RootExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationPluginTest {
  @Test
  void shouldCreateRootExtensionAndSetDynamicVersion() {
    var rootProject = ProjectBuilder.builder().withName("root").build();
    var childProject = ProjectBuilder.builder().withName("child").withParent(rootProject).build();
    rootProject.getPluginManager().apply("com.stano.application");
    assertNotNull(rootProject.getExtensions().findByType(RootExtension.class));
    assertInstanceOf(ProjectVersionProvider.class, rootProject.getVersion());
    assertInstanceOf(ProjectVersionProvider.class, childProject.getVersion());
  }
}
