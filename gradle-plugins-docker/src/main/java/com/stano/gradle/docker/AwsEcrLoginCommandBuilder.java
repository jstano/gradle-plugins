package com.stano.gradle.docker;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.api.GradleException;

public final class AwsEcrLoginCommandBuilder {
  private static final Pattern ECR_HOST_PATTERN =
      Pattern.compile("^\\d{12}\\.dkr\\.ecr\\.([a-z0-9-]+)\\.amazonaws\\.com(\\.cn)?$");
  private static final Pattern AWS_PROFILE_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]+$");

  private AwsEcrLoginCommandBuilder() {}

  public static boolean isEcrRegistry(String host) {
    return host != null && ECR_HOST_PATTERN.matcher(host).matches();
  }

  public static String extractRegion(String host) {
    Matcher matcher = ECR_HOST_PATTERN.matcher(host);
    if (!matcher.matches()) {
      throw new GradleException("Not an ECR registry host: " + host);
    }
    return matcher.group(1);
  }

  public static List<String> buildLoginCommand(String host, String awsProfile) {
    String region = extractRegion(host);
    StringBuilder command =
        new StringBuilder(AwsExecutable.resolve())
            .append(" ecr get-login-password --region ")
            .append(region);
    if (awsProfile != null) {
      if (!AWS_PROFILE_PATTERN.matcher(awsProfile).matches()) {
        throw new GradleException("Invalid AWS profile name: " + awsProfile);
      }
      command.append(" --profile ").append(awsProfile);
    }
    command
        .append(" | ")
        .append(DockerExecutable.resolve())
        .append(" login --username AWS --password-stdin ")
        .append(host);
    return List.of("bash", "-c", command.toString());
  }
}
