package com.stano.gradle.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

class AwsEcrLoginCommandBuilderTest {
  @Test
  void isEcrRegistryShouldReturnTrueForAStandardEcrHost() {
    assertTrue(
        AwsEcrLoginCommandBuilder.isEcrRegistry("123456789012.dkr.ecr.us-east-2.amazonaws.com"));
  }

  @Test
  void isEcrRegistryShouldReturnTrueForAChinaRegionEcrHost() {
    assertTrue(
        AwsEcrLoginCommandBuilder.isEcrRegistry(
            "123456789012.dkr.ecr.cn-north-1.amazonaws.com.cn"));
  }

  @Test
  void isEcrRegistryShouldReturnFalseForANonAwsHost() {
    assertFalse(AwsEcrLoginCommandBuilder.isEcrRegistry("docker.io"));
    assertFalse(AwsEcrLoginCommandBuilder.isEcrRegistry("ghcr.io"));
  }

  @Test
  void isEcrRegistryShouldReturnFalseForANonEcrAwsHost() {
    assertFalse(AwsEcrLoginCommandBuilder.isEcrRegistry("my-bucket.s3.amazonaws.com"));
  }

  @Test
  void isEcrRegistryShouldReturnFalseForNull() {
    assertFalse(AwsEcrLoginCommandBuilder.isEcrRegistry(null));
  }

  @Test
  void extractRegionShouldReturnTheRegionFromAnEcrHost() {
    assertEquals(
        "us-east-2",
        AwsEcrLoginCommandBuilder.extractRegion("123456789012.dkr.ecr.us-east-2.amazonaws.com"));
  }

  @Test
  void buildLoginCommandWithoutAProfileShouldOmitTheProfileFlag() {
    List<String> command =
        AwsEcrLoginCommandBuilder.buildLoginCommand(
            "123456789012.dkr.ecr.us-east-2.amazonaws.com", null);
    assertEquals(
        List.of(
            "bash",
            "-c",
            AwsExecutable.resolve()
                + " ecr get-login-password --region us-east-2 | "
                + DockerExecutable.resolve()
                + " login --username AWS"
                + " --password-stdin 123456789012.dkr.ecr.us-east-2.amazonaws.com"),
        command);
  }

  @Test
  void buildLoginCommandWithAProfileShouldIncludeTheProfileFlag() {
    List<String> command =
        AwsEcrLoginCommandBuilder.buildLoginCommand(
            "123456789012.dkr.ecr.us-east-2.amazonaws.com", "my-profile");
    assertEquals(
        List.of(
            "bash",
            "-c",
            AwsExecutable.resolve()
                + " ecr get-login-password --region us-east-2 --profile my-profile | "
                + DockerExecutable.resolve()
                + " login"
                + " --username AWS --password-stdin"
                + " 123456789012.dkr.ecr.us-east-2.amazonaws.com"),
        command);
  }

  @Test
  void buildLoginCommandWithAnInvalidProfileShouldThrow() {
    assertThrows(
        GradleException.class,
        () ->
            AwsEcrLoginCommandBuilder.buildLoginCommand(
                "123456789012.dkr.ecr.us-east-2.amazonaws.com", "bad profile; rm -rf /"));
  }
}
