package com.stano.gradle.settings;

import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class SettingsPluginTest {
  @Test
  void stanoVersionIsLoadedFromProperties() throws IOException {
    try (var stream = SettingsPlugin.class.getResourceAsStream("stano-plugins.properties")) {
      org.junit.jupiter.api.Assertions.assertNotNull(stream);
      var props = new Properties();
      props.load(stream);
      var version = props.getProperty("version");
      org.junit.jupiter.api.Assertions.assertNotNull(version);
      org.junit.jupiter.api.Assertions.assertFalse(version.isEmpty());
      org.junit.jupiter.api.Assertions.assertFalse(
          version.contains("$"), "Version should be expanded, not contain $");
    }
  }
}
