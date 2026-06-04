package com.stano.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.Files

class GradlePluginUtilSpec extends Specification {

   def "installResource should work"() {

      def resource = "testfile.txt"
      def outputFolder = Files.createTempDirectory(null).toFile()
      def outputFile = new File(outputFolder, resource)

      when:
      GradlePluginUtil.installResource(getClass().classLoader, outputFolder, resource)

      then:
      outputFile.exists()

      cleanup:
      outputFile.delete()
   }

   def "isWindows should return true if the os.name system property is 'windows'"() {

      def oldOsName = System.getProperty("os.name")
      System.setProperty("os.name", "windows")

      expect:
      GradlePluginUtil.isWindows()

      cleanup:
      System.setProperty("os.name", oldOsName)
   }

   def "getProperty should work"() {

      def rootProject = ProjectBuilder.builder().build()
      rootProject.ext.customProperty = 'ROOT'

      def childProject = ProjectBuilder.builder().withParent(rootProject).build()
      childProject.ext.customProperty = 'CHILD'

      def subProject = ProjectBuilder.builder().withParent(childProject).build()
      subProject.ext.customProperty = 'SUB'

      expect:
      GradlePluginUtil.getProperty(rootProject, 'customProperty') == 'ROOT'
      GradlePluginUtil.getProperty(childProject, 'customProperty') == 'CHILD'
      GradlePluginUtil.getProperty(subProject, 'customProperty') == 'SUB'

      GradlePluginUtil.getProperty(rootProject, 'projectDir').getClass().getName() == 'java.io.File'
      GradlePluginUtil.getProperty(childProject, 'projectDir').getClass().getName() == 'java.io.File'
      GradlePluginUtil.getProperty(subProject, 'projectDir').getClass().getName() == 'java.io.File'

      GradlePluginUtil.getProperty(rootProject, 'notFoundProperty') == null
      GradlePluginUtil.getProperty(childProject, 'notFoundProperty') == null
      GradlePluginUtil.getProperty(subProject, 'notFoundProperty') == null
   }

   def "isRunningInsideIde should return false if the idea.active property is not defined"() {

      System.clearProperty("idea.active")

      expect:
      !GradlePluginUtil.isRunningInsideIde()
   }

   def "isRunningInsideIde should return true or false based on the value of the idea.active property"() {

      System.setProperty("idea.active", ideaActive)

      expect:
      GradlePluginUtil.isRunningInsideIde() == expectedResult

      cleanup:
      System.clearProperty("idea.active")

      where:
      ideaActive | expectedResult
      "false"    | false
      "abc"      | false
      "true"     | true
   }

   def "notRunningInsideIde should return true if the idea.active property is not defined"() {

      expect:
      GradlePluginUtil.notRunningInsideIde()
   }

   def "notRunningInsideIde should return true or false based on the value of the idea.active property"() {

      System.setProperty("idea.active", ideaActive)

      expect:
      GradlePluginUtil.notRunningInsideIde() == expectedResult

      cleanup:
      System.clearProperty("idea.active")

      where:
      ideaActive | expectedResult
      "false"    | true
      "abc"      | true
      "true"     | false
   }

   def "getProjectProperty should work"() {

      def rootProject = ProjectBuilder.builder().withProjectDir(File.createTempDir('gradle-plugin-util-root', 'root')).build()
      rootProject.ext.outputPath = "/root"
      rootProject.projectDir.mkdirs()

      def childProject = ProjectBuilder.builder().withParent(rootProject).withProjectDir(new File(rootProject.projectDir, 'child')).build()
      childProject.ext.outputPath = "/child"
      childProject.projectDir.mkdirs()

      def subProject = ProjectBuilder.builder().withParent(childProject).withProjectDir(new File(childProject.projectDir, 'sub')).build()
      subProject.projectDir.mkdirs()

      expect:
      GradlePluginUtil.getProjectProperty(rootProject, "outputPath") == "/root"
      GradlePluginUtil.getProjectProperty(childProject, "outputPath") == "/child"
      GradlePluginUtil.getProjectProperty(subProject, "outputPath") == "/child"

      GradlePluginUtil.getProjectProperty(rootProject, "noDefaultValue") == null
      GradlePluginUtil.getProjectProperty(rootProject, "withDefaultValue", "the-default-value") == "the-default-value"

      cleanup:
      rootProject.projectDir.deleteDir()
   }

   def "getProjectOrSystemProperty should work"() {

      System.setProperty("custom.property", "custom-property-value")

      def rootProject = ProjectBuilder.builder().withProjectDir(File.createTempDir('gradle-plugin-util-root', 'root')).build()
      rootProject.ext.outputPath = "/root"
      rootProject.projectDir.mkdirs()

      def childProject = ProjectBuilder.builder().withParent(rootProject).withProjectDir(new File(rootProject.projectDir, 'child')).build()
      childProject.ext.outputPath = "/child"
      childProject.projectDir.mkdirs()

      expect:
      GradlePluginUtil.getProjectOrSystemProperty(rootProject, "outputPath") == "/root"
      GradlePluginUtil.getProjectOrSystemProperty(childProject, "outputPath") == "/child"

      GradlePluginUtil.getProjectOrSystemProperty(rootProject, "noDefaultValue") == null
      GradlePluginUtil.getProjectOrSystemProperty(rootProject, "withDefaultValue", "the-default-value") == "the-default-value"

      GradlePluginUtil.getProjectOrSystemProperty(rootProject, "custom.property") == "custom-property-value"

      cleanup:
      rootProject.projectDir.deleteDir()
   }

   def "getNestedProjectName should work"() {
      def rootProject = ProjectBuilder
         .builder()
         .withName("Gradle-Root-Project")
         .withProjectDir(File.createTempDir('gradle-plugin-util-root', 'root'))
         .build()
      rootProject.projectDir.mkdirs()

      def childProject = ProjectBuilder
         .builder()
         .withParent(rootProject)
         .withName("Gradle-Child-Project")
         .withProjectDir(new File(rootProject.projectDir, 'child'))
         .build()
      childProject.projectDir.mkdirs()

      expect:
      GradlePluginUtil.getNestedProjectName(rootProject) == "gradle-root-project"
      GradlePluginUtil.getNestedProjectName(childProject) == "gradle-root-project-gradle-child-project"

      cleanup:
      rootProject.projectDir.deleteDir()
   }
}
