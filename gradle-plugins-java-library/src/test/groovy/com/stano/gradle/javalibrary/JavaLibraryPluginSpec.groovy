package com.stano.gradle.javalibrary

import com.stano.gradle.plugin.test.BasePluginSpec
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.jvm.tasks.Jar
import spock.lang.Ignore

@Ignore
class JavaLibraryPluginSpec extends BasePluginSpec {
   def setup() {
      System.setProperty('com.r365.maven.url', 'https://gitlab.com/api/v4/projects/66116551/packages/maven')
      System.setProperty('com.r365.maven.private-token', 'abc123')
      System.setProperty('com.r365.sonar.host.url', 'http://localhost:9000')
      System.setProperty('com.r365.sonar.login', 'sonar')

      childProject.apply plugin: 'r365-java-library'
   }

   def "verify that the common plugins are applied"() {
      expect:
      childProject.plugins.hasPlugin('r365-java-module')
      childProject.plugins.hasPlugin('java-library')
      childProject.plugins.hasPlugin('jacoco')
      childProject.plugins.hasPlugin('maven-publish')
   }

   def "verify that the artifacts are configured properly"() {
      Jar jarTask = (Jar)childProject.tasks.findByName('jar')
      Jar sourcesJarTask = (Jar)childProject.tasks.findByName('sourcesJar')
      Jar javadocJarTask = (Jar)childProject.tasks.findByName('javadocJar')

      expect:
      hasExcludes(jarTask.rootSpec)
      hasExcludes(sourcesJarTask.rootSpec)
      hasExcludes(javadocJarTask.rootSpec)

      childProject.configurations.getByName('archives').artifacts.size() == 1
      childProject.configurations.getByName('archives').artifacts.files.size() == 1
      childProject.configurations.getByName('archives').artifacts.files.singleFile.name == 'test-1.2.3.jar'

      childProject.configurations.getByName('sourcesElements').artifacts.size() == 1
      childProject.configurations.getByName('sourcesElements').artifacts.files.size() == 1
      childProject.configurations.getByName('sourcesElements').artifacts.files.singleFile.name == 'test-1.2.3-sources.jar'

      childProject.configurations.getByName('javadocElements').artifacts.size() == 1
      childProject.configurations.getByName('javadocElements').artifacts.files.size() == 1
      childProject.configurations.getByName('javadocElements').artifacts.files.singleFile.name == 'test-1.2.3-javadoc.jar'
   }

   def "verify that the publish task is configured properly"() {
      expect:
      childProject.publishing.publications.size() == 1
      childProject.publishing.publications[0].groupId == 'root'
      childProject.publishing.publications[0].artifactId == 'test'
      childProject.publishing.publications[0].version == '1.2.3'

      childProject.publishing.repositories.size() == 1

      childProject.publishing.repositories[0].name == 'libs-release-local'
      childProject.publishing.repositories[0].url.toString() == 'https://artifactory.r365.com/artifactory/libs-release-local'
      childProject.publishing.repositories[0].credentials.username == 'MAVEN_USERNAME'
      childProject.publishing.repositories[0].credentials.password == 'MAVEN_PASSWORD'
   }

   private boolean hasExcludes(CopySpecInternal spec) {
      for (Iterator i = spec.children.iterator(); i.hasNext();) {
         def next = i.next()

         if (next.getExcludes().contains('**/.gitkeep')) {
            return true;
         }

         def result = hasExcludes(next)

         if (result) {
            return result
         }
      }

      return false;
   }
}
