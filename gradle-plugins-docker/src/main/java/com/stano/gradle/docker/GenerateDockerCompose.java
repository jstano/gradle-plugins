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

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "docker compose has no input or outputs")
public class GenerateDockerCompose extends DefaultTask {
    @Internal
    private Configuration configuration;

    public GenerateDockerCompose() {
        this.setGroup("Docker");
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @TaskAction
    void run() throws IOException {
        if (!getTemplate().isFile()) {
            throw new IllegalStateException("Could not find specified template file " + getTemplate().getAbsolutePath());
        }

        Map<String, String> templateTokens = new LinkedHashMap<>();

        for (ModuleVersionIdentifier id : getModuleDependencies()) {
            templateTokens.put("{{" + id.getGroup() + ":" + id.getName() + "}}", id.getVersion());
        }

        templateTokens.putAll(getExtraTemplateTokens().entrySet().stream()
            .collect(Collectors.toMap(e -> "{{" + e.getKey() + "}}", Map.Entry::getValue)));

        try (PrintWriter writer = new PrintWriter(getDockerComposeFile());
             BufferedReader reader = new BufferedReader(new FileReader(getTemplate()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(replaceAll(line, templateTokens));
            }
        }
    }

    @Internal
    @Override
    public String getDescription() {
        DockerComposeExtension ext = getDockerComposeExtension();
        String defaultDescription = "Populates " + ext.getTemplate().getName() + " file with versions"
                + " of dependencies from the '" + configuration.getName() + "' configuration";
        return super.getDescription() != null ? super.getDescription() : defaultDescription;
    }

    @Input
    Set<ModuleVersionIdentifier> getModuleDependencies() {
        return configuration.getResolvedConfiguration()
            .getResolvedArtifacts()
            .stream()
            .map(artifact -> artifact.getModuleVersion().getId())
            .collect(Collectors.toSet());
    }

    @Input
    Map<String, String> getExtraTemplateTokens() {
        return getDockerComposeExtension().getTemplateTokens();
    }

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    File getTemplate() {
        return getDockerComposeExtension().getTemplate();
    }

    @OutputFile
    File getDockerComposeFile() {
        return getDockerComposeExtension().getDockerComposeFile();
    }

    @Internal
    DockerComposeExtension getDockerComposeExtension() {
        return getProject().getExtensions().findByType(DockerComposeExtension.class);
    }

    protected String replaceAll(String line, Map<String, String> templateTokens) {
        for (Map.Entry<String, String> mapping : templateTokens.entrySet()) {
            line = line.replace(mapping.getKey(), mapping.getValue());
        }

        List<String> unmatchedTokens = new ArrayList<>();
        Matcher m = Pattern.compile("\\{\\{.*?\\}\\}").matcher(line);
        while (m.find()) {
            unmatchedTokens.add(m.group());
        }

        Preconditions.checkState(
            unmatchedTokens.isEmpty(),
            "Failed to resolve Docker dependencies declared in %s: %s. Known dependencies: %s",
            getTemplate(),
            unmatchedTokens,
            templateTokens);

        return line;
    }
}
