package com.stano.gradle

import spock.lang.Specification

class SystemEnvironmentSpec extends Specification {

   def "getAllEnvironmentVariables should return all values from System.getenv"() {

      def systemEnvironment = new SystemEnvironment()
      def all = systemEnvironment.getAllEnvironmentVariables()

      expect:
      all.equals(System.getenv())
   }

   def "getEnvironmentVariable should return the value using System.getenv"() {

      def systemEnvironment = new SystemEnvironment()
      def firstKey = System.getenv().keySet().first()

      expect:
      !systemEnvironment.getEnvironmentVariable("TEST")
      systemEnvironment.getEnvironmentVariable(firstKey) == System.getenv(firstKey)
   }
}
