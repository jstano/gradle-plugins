package com.stano.gradle.base;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.GradleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class BuildInfoUpdater {
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoUpdater.class);

  @SuppressWarnings("unchecked")
  public static void updateYmlWithBuildInfo(
      String contextName,
      String version,
      BuildInfoProvider buildInfoProvider,
      String applicationYmlPath) {
    LOGGER.info("updating the application.yml info data");
    File applicationYmlFile = new File(applicationYmlPath);
    if (applicationYmlFile.canRead()) {
      Yaml yaml = new Yaml();
      Map<String, Object> applicationYamlData = IOUtils.withReader(applicationYmlFile, yaml::load);
      if (applicationYamlData == null) {
        throw new GradleException("application.yml at " + applicationYmlPath + " is empty");
      }
      Map<String, Object> info = (Map<String, Object>) applicationYamlData.get("info");
      if (info == null) {
        throw new GradleException(
            "application.yml at " + applicationYmlPath + " is missing 'info' key");
      }
      Map<String, Object> appMap = (Map<String, Object>) info.get("app");
      if (appMap == null) {
        throw new GradleException(
            "application.yml at " + applicationYmlPath + " is missing 'info.app' key");
      }
      appMap.put("version", version);
      if (contextName != null) {
        appMap.put("name", contextName);
      }
      info.put("build", buildInfoMap(buildInfoProvider));
      IOUtils.withWriter(
          applicationYmlFile,
          writer -> {
            try {
              writer.write(yaml.dumpAsMap(applicationYamlData));
            } catch (IOException x) {
              throw new GradleException(x.getMessage());
            }
          });
    }
  }

  private static Map<String, String> buildInfoMap(BuildInfoProvider buildInfoProvider) {
    Map<String, String> buildInfoMap = new HashMap<>();
    buildInfoMap.put("number", buildInfoProvider.getBuildNumber());
    buildInfoMap.put("branch", buildInfoProvider.getBranchName());
    buildInfoMap.put("job", buildInfoProvider.getJobName());
    return buildInfoMap;
  }
}
