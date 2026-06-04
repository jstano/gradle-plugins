package com.stano.gradle.javamodule

import com.stano.gradle.plugin.test.BasePluginSpec
import org.gradle.api.JavaVersion
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.tasks.JacocoReport
import spock.lang.Ignore

@Ignore
class JavaModulePluginSpec extends BasePluginSpec {

   def setup() {
      System.setProperty('r365MavenUrl', 'https://artifactory.r365.com/artifactory/maven')
      System.setProperty('r365MavenUsername', 'MAVEN_USERNAME')
      System.setProperty('r365MavenPassword', 'MAVEN_PASSWORD')
   }

   def "if the r365-project plugin has not been applied then it should be applied to the root project"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      rootProject.plugins.hasPlugin('r365-project')

      childProject.r365.mavenUsername == "MAVEN_USERNAME"
      childProject.r365.mavenPassword == "MAVEN_PASSWORD"
      childProject.r365.setPlatformVersion == '5.0.0'
   }

   def "verify that the common plugins are applied"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.plugins.hasPlugin('r365-java-module')
      childProject.plugins.hasPlugin('java-library')
      childProject.plugins.hasPlugin('groovy')
      childProject.plugins.hasPlugin('jacoco')
      !childProject.plugins.hasPlugin('maven-publish')
   }

   def "verify that the sourceSets are configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.sourceSets.main.java.srcDirs.size() == 1
      childProject.sourceSets.main.java.srcDirs[0].path.endsWith("src${File.separator}main${File.separator}java")

      childProject.sourceSets.main.groovy.srcDirs.size() == 1
      childProject.sourceSets.main.groovy.srcDirs[0].path.endsWith("src${File.separator}main${File.separator}groovy")

      childProject.sourceSets.main.resources.srcDirs.size() == 1
      childProject.sourceSets.main.resources.srcDirs[0].path.endsWith("src${File.separator}main${File.separator}resources")

      childProject.sourceSets.test.java.srcDirs.size() == 1
      childProject.sourceSets.test.java.srcDirs[0].path.endsWith("src${File.separator}test${File.separator}java")

      childProject.sourceSets.test.groovy.srcDirs.size() == 1
      childProject.sourceSets.test.groovy.srcDirs[0].path.endsWith("src${File.separator}test${File.separator}groovy")

      childProject.sourceSets.test.resources.srcDirs.size() == 1
      childProject.sourceSets.test.resources.srcDirs[0].path.endsWith("src${File.separator}test${File.separator}resources")
   }

   def "verify that the repositories are configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.repositories.size() == 9

      childProject.repositories[0].name == 'MavenLocal'
      childProject.repositories[1].name == 'MavenRepo'

      childProject.repositories[2].name == 'r365-maven'
      childProject.repositories[2].url.toString() == 'https://artifactory.r365.com/artifactory/maven'
      childProject.repositories[2].credentials.username == "MAVEN_USERNAME"
      childProject.repositories[2].credentials.password == "MAVEN_PASSWORD"

      childProject.repositories[3].url.toString() == 'https://oss.sonatype.org/content/repositories/snapshots/'
      childProject.repositories[4].url.toString() == 'https://repo.spring.io/snapshot'
      childProject.repositories[5].url.toString() == 'https://repo.spring.io/milestone'
      childProject.repositories[6].url.toString() == 'https://www.gridgainsystems.com/nexus/content/repositories/external'
      childProject.repositories[7].url.toString() == 'https://repository.mulesoft.org/nexus/content/repositories/public/'
      childProject.repositories[8].url.toString() == 'https://build.shibboleth.net/nexus/content/repositories/releases'
   }

   def "verify that the default dependencies are configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.configurations.any {
         it.excludeRules.size() == 3 &&
         it.excludeRules[0].group == 'commons-logging' &&
         it.excludeRules[0].module == 'commons-logging' &&
         it.excludeRules[1].group == 'commons-logging' &&
         it.excludeRules[1].module == 'commons-logging-api' &&
         it.excludeRules[2].group == 'log4j' &&
         it.excludeRules[2].module == 'log4j'
      }

      childProject.configurations.getByName('implementation').dependencies.size() == 1
      childProject.configurations.getByName('implementation').dependencies[0].group == 'com.r365'
      childProject.configurations.getByName('implementation').dependencies[0].name == 'framework-bom'
      childProject.configurations.getByName('implementation').dependencies[0].version == '5.0.0'
//      project.configurations.getByName('implementation').dependencies[1].group == 'com.r365'
//      project.configurations.getByName('implementation').dependencies[1].name == 'framework-groovy'
//      project.configurations.getByName('implementation').dependencies[1].version == null

      childProject.configurations.getByName('testImplementation').dependencies.size() == 1
      childProject.configurations.getByName('testImplementation').dependencies[0].group == 'com.r365'
      childProject.configurations.getByName('testImplementation').dependencies[0].name == 'framework-test'
      childProject.configurations.getByName('testImplementation').dependencies[0].version == null
   }

   def "verify that the artifacts are configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      Jar jarTask = (Jar)childProject.tasks.findByName('jar')

      expect:
      !childProject.tasks.findByName('sourcesJar')
      !childProject.tasks.findByName('javadocJar')

      hasExcludes(jarTask.rootSpec)

      childProject.configurations.getByName('archives').artifacts.size() == 1
      childProject.configurations.getByName('archives').artifacts.files.size() == 1
      childProject.configurations.getByName('archives').artifacts.files.singleFile.name == 'test-1.2.3.jar'
   }

   def "verify that the compilers are configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.sourceCompatibility == JavaVersion.current()
      childProject.targetCompatibility == JavaVersion.current()

      childProject.compileJava.options.incremental
      childProject.compileJava.options.compilerArgs.size() == 4
      childProject.compileJava.options.compilerArgs.get(0) == '-Xlint:none'
      childProject.compileJava.options.compilerArgs.get(1) == '-Xdoclint:none'
      childProject.compileJava.options.compilerArgs.get(2) == '-nowarn'
      childProject.compileJava.options.compilerArgs.get(3) == '-parameters'
      childProject.compileJava.options.fork
      childProject.compileJava.options.forkOptions.jvmArgs.size() == 2
      childProject.compileJava.options.forkOptions.jvmArgs.get(0) == '-Xmx4096m'

      childProject.compileGroovy.options.incremental
      childProject.compileGroovy.options.compilerArgs.size() == 4
      childProject.compileGroovy.options.compilerArgs.get(0) == '-Xlint:none'
      childProject.compileGroovy.options.compilerArgs.get(1) == '-Xdoclint:none'
      childProject.compileGroovy.options.compilerArgs.get(2) == '-nowarn'
      childProject.compileGroovy.options.compilerArgs.get(3) == '-parameters'
      childProject.compileGroovy.groovyOptions.fork
      childProject.compileGroovy.groovyOptions.parameters
      childProject.compileGroovy.groovyOptions.forkOptions.jvmArgs.size() == 3
      childProject.compileGroovy.groovyOptions.forkOptions.jvmArgs.get(0) == '-Xmx4096m'
      childProject.compileGroovy.groovyOptions.forkOptions.jvmArgs.get(1) == '-Xss256m'
   }

   def "JavaVersion should return the proper object given just the major version number"() {
      expect:
      JavaVersion.toVersion("8") == JavaVersion.VERSION_1_8
      JavaVersion.toVersion("11") == JavaVersion.VERSION_11
      JavaVersion.toVersion("17") == JavaVersion.VERSION_17
   }

   def "should be able to override the java version using the standard gradle java plugin settings"() {
      childProject.apply plugin: 'r365-java-module'

      childProject.java {
         sourceCompatibility = javaVersion
         targetCompatibility = javaVersion
      }

      expect:
      childProject.sourceCompatibility == expectedJavaVersion
      childProject.targetCompatibility == expectedJavaVersion

      where:
      javaVersion              | expectedJavaVersion
      JavaVersion.VERSION_1_6  | JavaVersion.VERSION_1_6
      JavaVersion.VERSION_1_7  | JavaVersion.VERSION_1_7
      JavaVersion.VERSION_1_8  | JavaVersion.VERSION_1_8
      JavaVersion.VERSION_1_9  | JavaVersion.VERSION_1_9
      JavaVersion.VERSION_1_10 | JavaVersion.VERSION_1_10
      JavaVersion.VERSION_11   | JavaVersion.VERSION_11
      JavaVersion.VERSION_17   | JavaVersion.VERSION_17
   }

   def "verify that the test plugin is configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      childProject.test.minHeapSize == '512m'
      childProject.test.maxHeapSize == '4096m'
   }

   def "verify that the jacoco plugin is configured properly"() {
      childProject.apply plugin: 'r365-java-module'

      expect:
      JacocoReport jacocoTestReportTask = (JacocoReport)childProject.tasks.getByName('jacocoTestReport')
      jacocoTestReportTask.reports.getHtml().getRequired().get()
      jacocoTestReportTask.reports.getXml().getRequired().get()
      !jacocoTestReportTask.reports.getCsv().getRequired().get()

      def testTask = childProject.tasks.getByName('test')
      testTask.getFinalizedBy().getDependencies(jacocoTestReportTask).contains(jacocoTestReportTask)
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
