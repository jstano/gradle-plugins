package com.stano.gradle;

import com.stano.gradle.changecase.ConstantCase;
import org.gradle.api.Project;

public class GradleProperties {
  public static boolean booleanProperty(Project project, String propertyName) {
    return Boolean.parseBoolean(getProperty(project, propertyName, "false"));
  }

  public static boolean booleanProperty(
      Project project, String propertyName, boolean defaultValue) {
    return Boolean.parseBoolean(getProperty(project, propertyName, Boolean.toString(defaultValue)));
  }

  public static String getProperty(Project project, String propertyName) {
    return getProperty(project, propertyName, null);
  }

  public static String getProperty(Project project, String propertyName, String defaultValue) {
    final var propertyValue = project.findProperty(propertyName);
    if (propertyValue != null) {
      return propertyValue.toString();
    }
    if (System.getProperty(propertyName) != null) {
      return System.getProperty(propertyName);
    }
    if (System.getenv(ConstantCase.constantCase(propertyName)) != null) {
      return System.getenv(ConstantCase.constantCase(propertyName));
    }
    if (System.getenv(ConstantCase.constantCase(propertyName.replace("com.", ""))) != null) {
      return System.getenv(ConstantCase.constantCase(propertyName.replace("com.", "")));
    }
    return defaultValue;
  }
}
