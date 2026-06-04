package com.stano.gradle

import spock.lang.Specification

class OSSpec extends Specification {

   static oldName = System.getProperty("os.name")

   def cleanupSpec() {
      System.setProperty("os.name", oldName)
   }

   def "name method should return the lowercased version of the os name with any text after the first space removed"() {

      when:
      System.setProperty("os.name", osName)

      then:
      OS.name() == expectedResult

      where:
      osName      | expectedResult
      "Windows 7" | "windows"
      "Mac OSX"   | "mac"
      "Linux"     | "linux"
   }

   def "isWindows should return true when os.name is windows; otherwise false"() {

      when:
      System.setProperty("os.name", osName)

      then:
      OS.windows == expectedResult

      where:
      osName      | expectedResult
      "Windows 7" | true
      "Mac OSX"   | false
      "Linux"     | false
   }
}
