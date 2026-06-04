package com.stano.gradle

import org.gradle.api.GradleException
import spock.lang.Specification

class IOUtilsSpec extends Specification {

   def "should be able to write text to a file and read it back"() {

      def file = File.createTempFile("__ioutils__", "__test__")

      IOUtils.withWriter(file, {writer ->
         writer.println("test")
      })

      def result = IOUtils.withReader(file, { reader ->
         return reader.readLine()
      })

      expect:
      result == 'test'

      cleanup:
      file.delete()
   }

   def "an IOException in withReader should be converted to a GradleException"() {

      when:
      IOUtils.withReader(new File("/hopfully/this/is/a/bad/directory/text.txt"), {})

      then:
      def exception = thrown(GradleException)
      exception.message == 'Error reading file /hopfully/this/is/a/bad/directory/text.txt'
      exception.cause instanceof IOException
   }

   def "an IOException in withWriter should be converted to a GradleException"() {

      when:
      IOUtils.withWriter(new File("/hopfully/this/is/a/bad/directory/text.txt"), {})

      then:
      def exception = thrown(GradleException)
      exception.message == 'Error writing file /hopfully/this/is/a/bad/directory/text.txt'
      exception.cause instanceof IOException
   }
}
