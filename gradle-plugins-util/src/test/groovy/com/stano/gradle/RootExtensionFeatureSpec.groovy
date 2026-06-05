package com.stano.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification

class RootExtensionFeatureSpec extends Specification {
   def "verify we can apply the root extension"() {
      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("root", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("root") >> { args ->
            return extensionsMap.get(args[0])
         }
      }
      // https://gitlab.com/api/v4/projects/66116551/packages/maven
      def projectPropertiesMap = [
         "javaPlatformVersion": "1.2.3",
         "pactBrokerUrl": "https://pact.stano.com",
         "pactBrokerUsername": "pact-username",
         "pactBrokerPassword": "pact-password"
      ]
      def project = Mock(Project) {
         getExtensions() >> extensions
         hasProperty(_) >> { args ->
            return projectPropertiesMap.containsKey(args[0])
         }
         getProperties() >> projectPropertiesMap
         findProperty(_) >> { args ->
            return projectPropertiesMap.get(args[0])
         }
         getRootDir() >> rootDir
      }
      def rootExtensionFeature = new RootExtensionFeature()
      rootExtensionFeature.apply(project)

      def rootExtension = extensionsMap.get("root") as RootExtension

      expect:
      rootExtension.javaPlatformVersion == "1.2.3"
      rootExtension.pactBrokerUrl == "https://pact.stano.com"
      rootExtension.pactBrokerUsername == "pact-username"
      rootExtension.pactBrokerPassword == "pact-password"
      rootExtension.javaVersion == "21"

      cleanup:
      rootDir.delete()
   }

   def "the RootExtension should default the javaVersion to 21"() {
      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("root", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("root") >> { args ->
            return extensionsMap.get(args[0])
         }
      }
      def projectPropertiesMap = [:]
      def project = Mock(Project) {
         getExtensions() >> extensions
         hasProperty(_) >> { args ->
            return projectPropertiesMap.containsKey(args[0])
         }
         findProperty(_) >> { args ->
            return projectPropertiesMap.get(args[0])
         }
         getProperties() >> projectPropertiesMap
         getRootDir() >> rootDir
      }
      def rootExtensionFeature = new RootExtensionFeature()
      rootExtensionFeature.apply(project)

      def rootExtension = extensionsMap.get("root") as RootExtension

      expect:
      rootExtension.javaVersion == "21"

      cleanup:
      rootDir.delete()
   }

   def "the RootExtension should pick up the javaVersion from the project properties"() {

      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("root", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("root") >> { args ->
            return extensionsMap.get(args[0])
         }
      }
      def projectPropertiesMap = [
         "javaVersion": "25"
      ]
      def project = Mock(Project) {
         getExtensions() >> extensions
         hasProperty(_) >> { args ->
            return projectPropertiesMap.containsKey(args[0])
         }
         findProperty(_) >> { args ->
            return projectPropertiesMap.get(args[0])
         }
         getProperties() >> projectPropertiesMap
         getRootDir() >> rootDir
      }
      def rootExtensionFeature = new RootExtensionFeature()
      rootExtensionFeature.apply(project)

      def rootExtension = extensionsMap.get("root") as RootExtension

      expect:
      rootExtension.javaVersion == "25"

      cleanup:
      rootDir.delete()
   }
}
