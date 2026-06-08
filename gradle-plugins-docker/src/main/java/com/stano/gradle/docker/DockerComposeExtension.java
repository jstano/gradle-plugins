package com.stano.gradle.docker;

import org.gradle.api.Project;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
