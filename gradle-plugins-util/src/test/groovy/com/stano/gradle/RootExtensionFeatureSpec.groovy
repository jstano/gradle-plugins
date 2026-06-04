package com.stano.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification

class RootExtensionFeatureSpec extends Specification {
   def "verify we can apply the r365 extension"() {
      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("r365", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("r365") >> { args ->
            return extensionsMap.get(args[0])
         }
      }
      // https://gitlab.com/api/v4/projects/66116551/packages/maven
      def projectPropertiesMap = [
         "javaPlatformVersion": "1.2.3",
         "pactBrokerUrl": "https://pact.r365.com",
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
      def r365ExtensionFeature = new RootExtensionFeature()
      r365ExtensionFeature.apply(project)

      def r365Extension = extensionsMap.get("r365") as RootExtension

      expect:
      r365Extension.javaPlatformVersion == "1.2.3"
      r365Extension.pactBrokerUrl == "https://pact.r365.com"
      r365Extension.pactBrokerUsername == "pact-username"
      r365Extension.pactBrokerPassword == "pact-password"
      r365Extension.javaVersion == "21"

      cleanup:
      rootDir.delete()
   }

   def "the R365Extension should default the javaVersion to 21"() {
      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("r365", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("r365") >> { args ->
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
      def r365ExtensionFeature = new RootExtensionFeature()
      r365ExtensionFeature.apply(project)

      def r365Extension = extensionsMap.get("r365") as RootExtension

      expect:
      r365Extension.javaVersion == "21"

      cleanup:
      rootDir.delete()
   }

   def "the R365Extension should pick up the javaVersion from the project properties"() {

      def rootDir = File.createTempDir()
      def extensionsMap = [:]
      def extensions = Mock(ExtensionContainer) {
         add("r365", _) >> { args ->
            extensionsMap.put(args[0], args[1])
         }
         findByName("r365") >> { args ->
            return extensionsMap.get(args[0])
         }
      }
      def projectPropertiesMap = [
         "javaVersion": "17"
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
      def r365ExtensionFeature = new RootExtensionFeature()
      r365ExtensionFeature.apply(project)

      def r365Extension = extensionsMap.get("r365") as RootExtension

      expect:
      r365Extension.javaVersion == "17"

      cleanup:
      rootDir.delete()
   }
}
