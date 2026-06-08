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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;

public class DockerComposeExtension {
    private final Project project;
    private File template;
    private File dockerComposeFile;
    private Map<String, String> templateTokens;

    public DockerComposeExtension(Project project) {
        this.project = project;
        this.template = project.file("docker-compose.yml.template");
        this.dockerComposeFile = project.file("docker-compose.yml");
        this.templateTokens = new HashMap<>();
    }

    public void setTemplate(Object dockerComposeTemplate) {
        this.template = project.file(dockerComposeTemplate);
    }

    public void setDockerComposeFile(Object dockerComposeFile) {
        this.dockerComposeFile = project.file(dockerComposeFile);
    }

    public void setTemplateTokens(Map<String, String> templateTokens) {
        this.templateTokens = templateTokens;
    }

    public void templateToken(String key, String value) {
        this.templateTokens.put(key, value);
    }

    public Map<String, String> getTemplateTokens() {
        return templateTokens;
    }

    public File getTemplate() {
        return template;
    }

    public File getDockerComposeFile() {
        return dockerComposeFile;
    }
}
