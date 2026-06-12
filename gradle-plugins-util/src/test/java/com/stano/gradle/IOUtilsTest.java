package com.stano.gradle;

import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IOUtilsTest {
  @Test
  void shouldBeAbleToWriteTextToFileAndReadItBack() {
    File file;
    try {
      file = File.createTempFile("__ioutils__", "__test__");
      IOUtils.withWriter(
          file,
          w -> {
            try {
              w.write("test");
              w.newLine();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
      String result =
          IOUtils.withReader(
              file,
              r -> {
                try {
                  return r.readLine();
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
      assertEquals("test", result);
      file.delete();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void ioExceptionInWithReaderShouldBeConvertedToGradleException() {
    GradleException exception =
        assertThrows(
            GradleException.class,
            () ->
                IOUtils.withReader(
                    new File("/hopfully/this/is/a/bad/directory/text.txt"), r -> null));
    assertEquals(
        "Error reading file /hopfully/this/is/a/bad/directory/text.txt", exception.getMessage());
    assertInstanceOf(IOException.class, exception.getCause());
  }

  @Test
  void ioExceptionInWithWriterShouldBeConvertedToGradleException() {
    GradleException exception =
        assertThrows(
            GradleException.class,
            () ->
                IOUtils.withWriter(
                    new File("/hopfully/this/is/a/bad/directory/text.txt"), w -> {}));
    assertEquals(
        "Error writing file /hopfully/this/is/a/bad/directory/text.txt", exception.getMessage());
    assertInstanceOf(IOException.class, exception.getCause());
  }
}
