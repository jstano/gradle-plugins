package com.stano.gradle.settings;

import org.gradle.api.initialization.Settings;
import org.gradle.caching.configuration.BuildCacheConfiguration;

import java.util.Map;

public class BuildCache {
  public void configureBuildCache(Settings settings) {
    final var extraProperties = settings.getExtensions().getExtraProperties().getProperties();
    final var buildCacheType = getBuildCacheType(extraProperties);
    if (buildCacheType == null) {
      settings.buildCache(
          buildCacheConfiguration -> {
            localBuildCache(buildCacheConfiguration, extraProperties);
          });
    } else if (buildCacheType.equalsIgnoreCase("s3")) {
      settings.getPlugins().apply("com.github.burrunan.s3-build-cache");
      settings.buildCache(
          buildCacheConfiguration -> {
            localBuildCache(buildCacheConfiguration, extraProperties);
            s3BuildCache(settings, buildCacheConfiguration, extraProperties);
          });
    }
  }

  private void localBuildCache(
      BuildCacheConfiguration buildCacheConfiguration, Map<String, Object> extraProperties) {
    buildCacheConfiguration.local(
        directoryBuildCache ->
            directoryBuildCache.setEnabled(localBuildCacheEnabled(extraProperties)));
  }

  private void s3BuildCache(
      Settings settings,
      BuildCacheConfiguration buildCacheConfiguration,
      Map<String, Object> extraProperties) {
    final var buildCachePrefix = getBuildCachePrefix(settings);
    buildCacheConfiguration.remote(
        com.github.burrunan.s3cache.AwsS3BuildCache.class,
        buildCache -> {
          buildCache.setBucket(getS3Bucket(extraProperties));
          buildCache.setRegion(getS3Region(extraProperties));
          buildCache.setAwsAccessKeyId(getAccessKeyId(extraProperties));
          buildCache.setAwsSecretKey(getSecretAccessKey(extraProperties));
          buildCache.setPrefix(buildCachePrefix + "/");
          buildCache.setPush(getPushEnabled(extraProperties));
        });
  }

  private boolean localBuildCacheEnabled(Map<String, Object> extraProperties) {
    return Boolean.parseBoolean(
        extraProperties.getOrDefault("com.stano.build-cache.local.enabled", "true").toString());
  }

  private String getBuildCacheType(Map<String, Object> extraProperties) {
    final var buildCacheType = System.getenv("STANO_BUILD_CACHE_TYPE");
    if (buildCacheType != null) {
      return buildCacheType;
    }
    final var type = extraProperties.get("com.stano.build-cache.type");
    return type != null ? type.toString() : null;
  }

  private String getS3Bucket(Map<String, Object> extraProperties) {
    final var s3Bucket = System.getenv("STANO_BUILD_CACHE_S3_BUCKET");
    if (s3Bucket != null) {
      return s3Bucket;
    }
    final var bucket = extraProperties.get("com.stano.build-cache.s3.bucket");
    if (bucket == null) {
      throw new IllegalStateException(
          "S3 build cache enabled but com.stano.build-cache.s3.bucket or STANO_BUILD_CACHE_S3_BUCKET not configured");
    }
    return bucket.toString();
  }

  private String getS3Region(Map<String, Object> extraProperties) {
    final var s3Region = System.getenv("STANO_BUILD_CACHE_S3_REGION");
    if (s3Region != null) {
      return s3Region;
    }
    final var region = extraProperties.get("com.stano.build-cache.s3.region");
    if (region == null) {
      throw new IllegalStateException(
          "S3 build cache enabled but com.stano.build-cache.s3.region or STANO_BUILD_CACHE_S3_REGION not configured");
    }
    return region.toString();
  }

  private String getAccessKeyId(Map<String, Object> extraProperties) {
    final var accessKeyId = System.getenv("STANO_BUILD_CACHE_S3_ACCESS_KEY_ID");
    if (accessKeyId != null) {
      return accessKeyId;
    }
    final var keyId = extraProperties.get("com.stano.build-cache.s3.access-key-id");
    if (keyId == null) {
      throw new IllegalStateException(
          "S3 build cache enabled but com.stano.build-cache.s3.access-key-id or STANO_BUILD_CACHE_S3_ACCESS_KEY_ID not configured");
    }
    return keyId.toString();
  }

  private String getSecretAccessKey(Map<String, Object> extraProperties) {
    final var secretAccessKey = System.getenv("STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY");
    if (secretAccessKey != null) {
      return secretAccessKey;
    }
    final var secret = extraProperties.get("com.stano.build-cache.s3.secret-access-key");
    if (secret == null) {
      throw new IllegalStateException(
          "S3 build cache enabled but com.stano.build-cache.s3.secret-access-key or STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY not configured");
    }
    return secret.toString();
  }

  private boolean getPushEnabled(Map<String, Object> extraProperties) {
    final var pushEnabled = System.getenv("STANO_BUILD_CACHE_PUSH_ENABLED");
    if (pushEnabled != null) {
      return Boolean.parseBoolean(pushEnabled);
    }
    return Boolean.parseBoolean(
        extraProperties.getOrDefault("com.stano.build-cache.push-enabled", "false").toString());
  }

  private String getBuildCachePrefix(Settings settings) {
    final var buildCacheSettings =
        settings.getExtensions().getByType(BuildCacheSettingsExtension.class);
    return buildCacheSettings.getBuildCachePrefix().getOrElse(settings.getRootProject().getName());
  }
}
