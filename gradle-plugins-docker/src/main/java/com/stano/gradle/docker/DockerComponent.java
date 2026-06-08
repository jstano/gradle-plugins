/*
 * (c) Copyright 2020 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stano.gradle.docker;

import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DockerComponent implements SoftwareComponentInternal {

   private final Set<PublishArtifact> artifacts = new LinkedHashSet<>();

   public DockerComponent(PublishArtifact dockerArtifact) {

      artifacts.add(dockerArtifact);
   }

   @Override
   public final String getName() {

      return "docker";
   }

   @Override
   public final Set<UsageContext> getUsages() {

      return Collections.emptySet();
   }
}
