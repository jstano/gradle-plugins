package com.stano.gradle.settings;

import org.gradle.api.provider.Property;

public interface BuildCacheSettingsExtension {
  Property<String> getBuildCachePrefix();
}
