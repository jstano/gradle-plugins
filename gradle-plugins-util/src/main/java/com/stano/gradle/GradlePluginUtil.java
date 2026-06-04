package com.stano.gradle;

import com.stano.gradle.changecase.ConstantCase;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GradlePluginUtil {
  public static void installResource(ClassLoader classLoader, File folder, String resource) {
    copyStreamToFile(getResourceAsStream(classLoader, resource), new File(folder, resource));
  }

  private static InputStream getResourceAsStream(ClassLoader classLoader, String resource) {
    return classLoader.getResourceAsStream(resource);
  }

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  @SuppressWarnings("unchecked")
  public static <T> T getProperty(Project project, String propertyName) {
    while (project != null) {
      if (project.hasProperty(propertyName)) {
        return (T)project.property(propertyName);
      }

      project = project.getParent();
    }

    return null;
  }

  public static String getProjectProperty(Project project, String propertyName) {
    return getProjectProperty(project, propertyName, null);
  }

  public static String getProjectProperty(Project project, String propertyName, String defaultValue) {
    if (project.hasProperty(propertyName)) {
      return project.findProperty(propertyName).toString();
    }

    return defaultValue;
  }

  public static String getProjectOrSystemProperty(Project project, String propertyName) {
    return getProjectOrSystemProperty(project, propertyName, null);
  }

  public static String getProjectOrSystemProperty(Project project, String propertyName, String defaultValue) {
    if (project.hasProperty(propertyName)) {
      return project.findProperty(propertyName).toString();
    }

    if (System.getProperty(propertyName) != null) {
      return System.getProperty(propertyName);
    }

    if (System.getenv(ConstantCase.constantCase(propertyName)) != null) {
      return System.getenv(ConstantCase.constantCase(propertyName));
    }

    return defaultValue;
  }

  public static boolean isRunningInsideIde() {
    return Boolean.parseBoolean(System.getProperty("idea.active", "false"));
  }

  public static boolean notRunningInsideIde() {
    return !isRunningInsideIde();
  }

  public static String getHostName() {
    try {
      return InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException x) {
      return "UNKNOWN_HOST";
    }
  }

  private static void copyStreamToFile(InputStream inputStream, File outputFile) {
    try {
      try (OutputStream outputStream = new FileOutputStream(outputFile)) {
        IOGroovyMethods.leftShift(outputStream, inputStream);
      }
    }
    catch (IOException x) {
      throw new RuntimeException(x);
    }
  }

  public static Task getExistingOrDefaultTask(TaskContainer tasks, String taskName) {
    Task result;
    try {
      result = tasks.getByName(taskName);
    }
    catch (Exception e) {
      result = tasks.register(taskName).get();
    }
    return result;
  }

  public static String getNestedProjectName(Project project) {
    Project rootProject = project.getRootProject();
    String extensionContextName = null;

    try {
      RootExtension rootRootExtension = rootProject.getExtensions().getByType(RootExtension.class);
      if (rootRootExtension != null) {
        extensionContextName = rootRootExtension.getContextName();
      }
      else {
        project.getLogger().lifecycle("R365Extension not found on project: " + project.getName());
      }
    }
    catch (Exception ex) {
      project.getLogger().lifecycle("R365Extension contextName not found on project: " + project.getName());
    }

    String rootProjectName = extensionContextName == null
                             ? rootProject.getName().toLowerCase()
                             : extensionContextName.toLowerCase();

    String projectName = project.getName().toLowerCase();

    if (rootProjectName.equals(projectName)) {
      return projectName;
    }

    return String.format("%s-%s", rootProjectName, projectName);
  }
}
