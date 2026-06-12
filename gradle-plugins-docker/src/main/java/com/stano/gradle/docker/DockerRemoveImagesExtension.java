package com.stano.gradle.docker;

import java.util.Collection;
import org.gradle.api.provider.Property;

public interface DockerRemoveImagesExtension {
  Property<Collection<String>> getImages();

  Property<Boolean> getForce();

  Property<Boolean> getNoPrune();
}
