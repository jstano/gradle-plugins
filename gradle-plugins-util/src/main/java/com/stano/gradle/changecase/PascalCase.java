package com.stano.gradle.changecase;

import static com.stano.gradle.changecase.NoCase.noCase;

public class PascalCase {

   /**
    * Transform into a string of capitalized words without separators.
    * "test string" becomes "TestString"
    *
    * @param text The text to transform
    * @return The transformed text
    */
   public static String pascalCase(String text) {

      return noCase(text, Options.options()
                                 .withDelimiter("")
                                 .withTransform(pascalCaseTransform));
   }

   public static Transform pascalCaseTransform = new PascalCaseTransform();
   public static Transform pascalCaseTransformMerge = new PascalCaseTransformMerge();

   private static class PascalCaseTransform implements Transform {

      @Override
      public String transform(String part, int index, String[] parts) {

         char firstChar = part.charAt(0);
         String lowerChars = part.substring(1).toLowerCase();

         if (index > 0 && firstChar >= '0' && firstChar <= '9') {
            return "_" + firstChar + lowerChars;
         }

         return Character.toUpperCase(firstChar) + lowerChars;
      }
   }

   private static class PascalCaseTransformMerge implements Transform {

      @Override
      public String transform(String part, int index, String[] parts) {

         return Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
      }
   }
}
