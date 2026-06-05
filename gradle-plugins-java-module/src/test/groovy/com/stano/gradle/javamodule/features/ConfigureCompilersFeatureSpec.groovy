package com.stano.gradle.javamodule.features

import com.stano.gradle.JavaVersionProvider
import com.stano.gradle.RootExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.ForkOptions
import org.gradle.api.tasks.compile.JavaCompile
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class ConfigureCompilersFeatureSpec extends Specification {
   def "ConfigureCompilersFeature should throw a GradleException if the current java version is not compatible with the project version"() {
      def javaVersionProvider = Mock(JavaVersionProvider) {
         currentVersion() >> JavaVersion.VERSION_1_8
      }
      def rootExtension = Mock(RootExtension) {
         getJavaVersion() >> "21"
        getJavaPlatformVersion() >> "5.0.0"
      }
      def project = Mock(Project) {
         getRootProject() >> Mock(Project) {
            getExtensions() >> Mock(ExtensionContainer) {
              getByType(RootExtension.class) >> rootExtension
            }
         }
      }
      def configureCompilersFeature = new ConfigureCompilersFeature(javaVersionProvider)

      when:
      configureCompilersFeature.apply(project)

      then:
      def exception = thrown(GradleException)
      exception.message == "The current java version ${javaVersionProvider.currentVersion()} is not compatible with the project java version 17"
   }

   def "ConfigureCompilersFeature should set the source and target compatibility on the project if the current java version is newer than the project java version"() {
      def javaVersionProvider = Mock(JavaVersionProvider) {
         currentVersion() >> JavaVersion.VERSION_17
      }
      def rootExtension = Mock(RootExtension) {
         getJavaVersion() >> "8"
        getJavaPlatformVersion() >> "5.0.0"
      }
      def javaReleaseSet = false
      def javaCompileOptionsMap = [:]
      def javaCompileOptions = Mock(CompileOptions) {
         setIncremental(true) >> {
            javaCompileOptionsMap.put('incremental', true)
         }
         setFork(true) >> {
            javaCompileOptionsMap.put('fork', true)
         }
         getForkOptions() >> Mock(ForkOptions) {
            setJvmArgs(_) >> { args ->
               javaCompileOptionsMap.put('compilerArgs', args[0])
            }
         }
         setCompilerArgs(_) >> { args ->
            javaCompileOptionsMap.put('compilerArgs', args[0])
         }
      }
      def javaCompile = Mock(JavaCompile) {
         setSourceCompatibility(_) >> { args ->
            javaCompileOptionsMap.put('sourceCompatibility', args[0])
         }
         setTargetCompatibility(_) >> { args ->
            javaCompileOptionsMap.put('targetCompatibility', args[0])
         }
         getOptions() >> javaCompileOptions
      }
      def project = Mock(Project) {
         getRootProject() >> Mock(Project) {
            getExtensions() >> Mock(ExtensionContainer) {
              getByType(RootExtension.class) >> rootExtension
            }
         }
         getTasks() >> Mock(TaskContainer) {
            withType(JavaCompile.class, _) >> { args ->
               ((Action)args[1]).execute(javaCompile)
            }
         }
      }
      def configureCompilersFeature = new ConfigureCompilersFeature()

      when:
      configureCompilersFeature.apply(project)

      then:
      javaCompileOptionsMap.get('sourceCompatibility') == '1.8'
      javaCompileOptionsMap.get('targetCompatibility') == '1.8'
      javaCompileOptionsMap.get('incremental') == true
      javaReleaseSet
   }
}
