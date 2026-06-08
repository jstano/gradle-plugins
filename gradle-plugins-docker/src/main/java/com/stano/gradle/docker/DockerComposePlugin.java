package com.stano.gradle.docker;

import com.google.common.collect.ImmutableMap;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class DockerComposePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("dockerCompose", DockerComposeExtension.class, project);
        Configuration dockerConfiguration = project.getConfigurations().maybeCreate("docker");

        project.subprojects(subproject -> {
            subproject.getPlugins().withId("com.palantir.product-dependency-introspection", ignored -> {
                dockerConfiguration.getDependencies().add(subproject.getDependencies().project(ImmutableMap.of(
                    "path", subproject.getPath(),
                    "configuration", "productDependencies"
                )));
            });
        });

        project.getPlugins().withId("com.palantir.product-dependency-introspection", ignored -> {
            dockerConfiguration.extendsFrom(project.getConfigurations().getByName("productDependencies"));
        });

        project.getTasks().create("generateDockerCompose", GenerateDockerCompose.class, task -> {
            task.setConfiguration(dockerConfiguration);
        });
        project.getTasks().create("dockerComposeUp", DockerComposeUp.class);
        project.getTasks().create("dockerComposeDown", DockerComposeDown.class);
    }
}
