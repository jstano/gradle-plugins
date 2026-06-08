package com.stano.gradle.docker;

import org.gradle.api.provider.Property;

import java.util.Collection;

public interface DockerRemoveImagesExtension {

   Property<Collection<String>> getImages();
   Property<Boolean> getForce();
   Property<Boolean> getNoPrune();
}
