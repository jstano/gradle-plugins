package com.stano.gradle

import org.gradle.api.GradleException
import spock.lang.Specification

class JsonReaderSpec extends Specification {

   def "should be able to read a json file with comments in it"() {

      def appJsonFile = File.createTempFile('app.json', null)
      appJsonFile.withWriter {
         it.println('// this is a comment')
         it.println('{"name":"the-name"}')
      }

      def jsonReader = new JsonReader()

      expect:
      jsonReader.readJsonFile(appJsonFile).get("name").asText() == 'the-name'

      cleanup:
      appJsonFile.delete()
   }

   def "if an IOException is thrown, it should be converted to a GradleException"() {

      def appJsonFile = File.createTempFile('app.json', null)
      appJsonFile.withWriter {
         it.println('// this is a comment')
         it.println('{"name":"the-name"}')
      }
      appJsonFile.delete()

      def jsonReader = new JsonReader()

      when:
      jsonReader.readJsonFile(appJsonFile)

      then:
      def exception = thrown(GradleException)
      exception.message == "Error reading file ${appJsonFile.absolutePath}"
   }
}
