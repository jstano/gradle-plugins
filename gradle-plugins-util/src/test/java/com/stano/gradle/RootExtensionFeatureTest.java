package com.stano.gradle;

import org.junit.jupiter.api.Test;

class RootExtensionFeatureTest {
  @Test
  void shouldCreateRootExtensionFeature() {
    RootExtensionFeature feature = new RootExtensionFeature();
    assertNotNull(feature);
  }

  private void assertNotNull(Object obj) {
    if (obj == null) {
      throw new AssertionError("Object was null");
    }
  }
}
