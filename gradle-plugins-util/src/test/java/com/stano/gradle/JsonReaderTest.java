package com.stano.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

class JsonReaderTest {
  @Test
  void shouldBeAbleToReadJsonFileWithComments() throws IOException {
    File appJsonFile = File.createTempFile("app.json", null);
    Files.writeString(appJsonFile.toPath(), "// this is a comment\n{\"name\":\"the-name\"}");
    JsonReader jsonReader = new JsonReader();
    assertEquals("the-name", jsonReader.readJsonFile(appJsonFile).get("name").asText());
    appJsonFile.delete();
  }

  @Test
  void shouldConvertIOExceptionToGradleException() throws IOException {
    File appJsonFile = File.createTempFile("app.json", null);
    appJsonFile.delete();
    JsonReader jsonReader = new JsonReader();
    GradleException exception =
        assertThrows(GradleException.class, () -> jsonReader.readJsonFile(appJsonFile));
    assertEquals("Error reading file " + appJsonFile.getAbsolutePath(), exception.getMessage());
  }
}
