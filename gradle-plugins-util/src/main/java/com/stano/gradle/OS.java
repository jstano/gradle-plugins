package com.stano.gradle;

public final class OS {

   public static String name() {

      String osName = System.getProperty("os.name").toLowerCase();

      if (osName.contains(" ")) {
         return osName.substring(0, osName.indexOf(' '));
      }

      return osName;
   }

   public static boolean isLinux() {

      return name().equals("linux");
   }

   public static boolean isMac() {

      return name().equals("mac");
   }

   public static boolean isWindows() {

      return name().equals("windows");
   }

   private OS() {

   }
}
