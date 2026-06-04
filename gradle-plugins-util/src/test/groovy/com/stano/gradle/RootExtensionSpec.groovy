package com.stano.gradle

import spock.lang.Specification

import java.time.LocalDateTime

class RootExtensionSpec extends Specification {
   def "should be able to get and set data from R365Extension"() {
      def r365Extension = new RootExtension()
      r365Extension.pactBrokerUrl = "pactBrokerUrl"
      r365Extension.pactBrokerUsername = "pactBrokerUsername"
      r365Extension.pactBrokerPassword = "pactBrokerPassword"
      r365Extension.pactBrokerToken = "pactBrokerToken"
      r365Extension.javaVersion = "javaVersion"
      r365Extension.contextName = "contextName"
      r365Extension.useSemVer = true
      r365Extension.buildTime = LocalDateTime.of(2022, 10, 6, 8, 30, 45)

      expect:
      r365Extension.pactBrokerUrl == "pactBrokerUrl"
      r365Extension.pactBrokerUsername == "pactBrokerUsername"
      r365Extension.pactBrokerPassword == "pactBrokerPassword"
      r365Extension.pactBrokerToken == "pactBrokerToken"
      r365Extension.javaVersion == "javaVersion"
      r365Extension.contextName == "contextName"
      r365Extension.useSemVer
      r365Extension.buildTime == LocalDateTime.of(2022, 10, 6, 8, 30, 45)
      r365Extension.buildTimeFormatted == "20221006083045"
   }
}
