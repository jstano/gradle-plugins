package com.stano.gradle.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.stano.gradle.base.features.BaseExtensionFeature;
import org.junit.jupiter.api.Test;

class BaseExtensionFeatureTest {
  @Test
  void shouldCreateBaseExtensionFeature() {
    BaseExtensionFeature feature = new BaseExtensionFeature();
    assertNotNull(feature);
  }
}
