package com.stano.gradle;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonReader {

   public JsonNode readJsonFile(File jsonFile) {

      try {
         try (FileReader reader = new FileReader(jsonFile)) {
            JsonFactory jsonFactory = new JsonFactory();
            jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);

            return new ObjectMapper(jsonFactory).reader().readTree(reader);
         }
      }
      catch (IOException x) {
         throw new GradleException(String.format("Error reading file %s", jsonFile.getAbsolutePath()), x);
      }
   }
}
