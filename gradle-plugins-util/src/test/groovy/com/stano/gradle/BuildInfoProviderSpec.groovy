package com.stano.gradle

import spock.lang.Specification

class BuildInfoProviderSpec extends Specification {

   def "BuildInfo should return the values from the environment"() {

      Environment environment = Mock(Environment) {
         getEnvironmentVariable("BUILD_NUMBER") >> '123'
         getEnvironmentVariable("BRANCH_NAME") >> 'main'
         getEnvironmentVariable("JOB_NAME") >> 'job/dev/job/taps/job/main'
      }

      def buildInfo = new BuildInfoProvider(environment)

      expect:
      buildInfo.buildNumber == '123'
      buildInfo.branchName == 'main'
      buildInfo.jobName == 'job/dev/job/taps/job/main'
   }

   def "BuildInfo should return 'unspecified' if the environment doesn't have the values in it"() {

      def buildInfo = new BuildInfoProvider(Mock(Environment))

      expect:
      buildInfo.buildNumber == 'unspecified'
      buildInfo.branchName == 'unspecified'
      buildInfo.jobName == 'unspecified'
   }
}
