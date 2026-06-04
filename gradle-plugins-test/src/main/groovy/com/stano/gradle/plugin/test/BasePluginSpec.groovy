package com.stano.gradle.plugin.test

import com.stano.gradle.RootExtensionFeature
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BasePluginSpec extends Specification {
   Project rootProject
   Project childProject

   def setup() {
      rootProject = ProjectBuilder.builder()
                                  .withName("root")
                                  .withProjectDir(File.createTempDir("gradle-test", "root"))
                                  .build()
      rootProject.version = "1.2.3"
//      rootProject.ext.platformVersion = "1.0.0"
//      rootProject.ext.gradleVersion = "8.14"
      rootProject.projectDir.mkdirs()
      new RootExtensionFeature().apply(rootProject)

      childProject = ProjectBuilder.builder()
                                   .withName("child")
                                   .withProjectDir(new File(rootProject.projectDir, "child"))
                                   .withParent(rootProject)
                                   .build()
      childProject.version = "1.2.3"
      childProject.projectDir.mkdirs()
   }

   def cleanup() {
      rootProject.projectDir.deleteDir()
   }
}
