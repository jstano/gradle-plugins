package com.stano.gradle;

import org.gradle.api.GradleException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IOUtils {
  public static <T> T withReader(File file, Function<BufferedReader, T> function) {
    try {
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        return function.apply(reader);
      }
    } catch (IOException x) {
      throw new GradleException("Error reading file " + file, x);
    }
  }

  public static void withWriter(File file, Consumer<BufferedWriter> consumer) {
    try {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        consumer.accept(writer);
      }
    } catch (IOException x) {
      throw new GradleException("Error writing file " + file, x);
    }
  }

  public static String readText(File file) {
    try {
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    } catch (IOException x) {
      throw new GradleException("Error reading file " + file, x);
    }
  }

  public static String readFirstLine(File file) {
    try {
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        return reader.readLine();
      }
    } catch (IOException x) {
      throw new GradleException("Error reading file " + file, x);
    }
  }
}
