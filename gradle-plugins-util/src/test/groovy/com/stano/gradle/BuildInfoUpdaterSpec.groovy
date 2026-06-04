package com.stano.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

class BuildInfoUpdaterSpec extends Specification {
   Project project

   def setup() {
      project = ProjectBuilder.builder()
                              .withName('test')
                              .withProjectDir(File.createTempDir('gradle-project-plugin-test', 'test'))
                              .build()
      project.version = '4.5.6'
      project.projectDir.mkdirs()
      project.file("build/resources/test").mkdirs()
      new RootExtensionFeature().apply(project);
   }

   def cleanup() {
      project.projectDir.delete()
   }

   def "should be able to update all the settings in the application.yml"() {
      Environment environment = Mock(Environment) {
         getEnvironmentVariable("BUILD_NUMBER") >> '123'
         getEnvironmentVariable("BRANCH_NAME") >> 'main'
         getEnvironmentVariable("JOB_NAME") >> 'job/dev/job/taps/job/main'
      }
      def r365Extension = Mock(RootExtension)
      def buildInfo = new BuildInfoProvider(environment)

      def ant = new AntBuilder()
      ant.copy(file: 'src/test/resources/application.yml', todir: "$project.buildDir/resources/test")

      def applicationYmlFile = new File("$project.buildDir/resources/test/application.yml")

      BuildInfoUpdater.updateYmlWithBuildInfo(r365Extension, project.version.toString(), buildInfo, "$project.buildDir/resources/test/application.yml")

      def applicationYamlData = null

      applicationYmlFile.withReader { reader ->
         applicationYamlData = new Yaml().load(reader)
      }

      expect:
      applicationYamlData.info.app.name == 'Test App Name'
      applicationYamlData.info.app.description == 'Test App Description'
      applicationYamlData.info.app.version == project.getVersion().toString()
      applicationYamlData.info.build.number == '123'
      applicationYamlData.info.build.branch == 'main'
      applicationYamlData.info.build.job == 'job/dev/job/taps/job/main'
   }

   def "if no application.yml file exists then nothing should be written"() {
      Environment environment = Mock(Environment) {
         getEnvironmentVariable("BUILD_NUMBER") >> '123'
         getEnvironmentVariable("BRANCH_NAME") >> 'main'
         getEnvironmentVariable("JOB_NAME") >> 'job/dev/job/taps/job/main'
      }
      def r365Extension = Mock(RootExtension)
      def buildInfo = new BuildInfoProvider(environment)

      def applicationYmlFile = new File("$project.layout.buildDirectory/resources/test/application.yml")

      BuildInfoUpdater.updateYmlWithBuildInfo(r365Extension, project.version.toString(), buildInfo, "$project.buildDir/resources/test/application.yml")

      expect:
      !applicationYmlFile.exists()
   }
}
