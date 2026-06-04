package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class SnakeCase {

   /**
    * Transform into a lower case string with underscores between words.
    * "test string" becomes "test_string"
    * @param text The text to transform
    * @return The transformed text
    */
   public static String snakeCase(String text) {

      return noCase(text, Options.options().withDelimiter("_"));
   }
}
