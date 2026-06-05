package com.stano.gradle

import spock.lang.Specification

import java.time.LocalDateTime

class RootExtensionSpec extends Specification {
   def "should be able to get and set data from RootExtension"() {
      def rootExtension = new RootExtension()
      rootExtension.pactBrokerUrl = "pactBrokerUrl"
      rootExtension.pactBrokerUsername = "pactBrokerUsername"
      rootExtension.pactBrokerPassword = "pactBrokerPassword"
      rootExtension.pactBrokerToken = "pactBrokerToken"
      rootExtension.javaVersion = "javaVersion"
      rootExtension.contextName = "contextName"
      rootExtension.useSemVer = true
      rootExtension.buildTime = LocalDateTime.of(2022, 10, 6, 8, 30, 45)

      expect:
      rootExtension.pactBrokerUrl == "pactBrokerUrl"
      rootExtension.pactBrokerUsername == "pactBrokerUsername"
      rootExtension.pactBrokerPassword == "pactBrokerPassword"
      rootExtension.pactBrokerToken == "pactBrokerToken"
      rootExtension.javaVersion == "javaVersion"
      rootExtension.contextName == "contextName"
      rootExtension.useSemVer
      rootExtension.buildTime == LocalDateTime.of(2022, 10, 6, 8, 30, 45)
      rootExtension.buildTimeFormatted == "20221006083045"
   }
}
