package com.stano.gradle.mavencentralpublish.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Uploads to a remote service; not a reusable build output")
public abstract class PublishToMavenCentralTask extends DefaultTask {
  @InputFile
  @PathSensitive(PathSensitivity.NONE)
  public abstract RegularFileProperty getStagingZip();

  @Input
  public abstract Property<String> getUploadName();

  @Input
  public abstract Property<String> getCentralToken();

  @TaskAction
  public void upload() {
    String token = getCentralToken().getOrNull();
    if (token == null || token.isBlank()) {
      throw new GradleException(
          "No Maven Central token configured. Set the property named by"
              + " mavenCentralPublish.centralTokenPropertyName, or the MAVEN_TOKEN environment"
              + " variable.");
    }

    File zipFile = getStagingZip().get().getAsFile();
    if (!zipFile.exists()) {
      throw new GradleException(
          "Staging zip not found at " + zipFile + " -- run zipStagingDeploy first.");
    }

    String boundary = "----MavenCentralPublish" + UUID.randomUUID();
    byte[] body = buildMultipartBody(boundary, zipFile);

    URI uri =
        URI.create(
            "https://central.sonatype.com/api/v1/publisher/upload?name="
                + getUploadName().get()
                + "&publishingType=AUTOMATIC");

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build();

    try {
      HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() / 100 != 2) {
        throw new GradleException(
            "Central Portal upload failed with status "
                + response.statusCode()
                + ": "
                + response.body());
      }
      getLogger().lifecycle("Uploaded to Central Portal: " + response.body());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new GradleException("Central Portal upload was interrupted", e);
    }
  }

  private byte[] buildMultipartBody(String boundary, File zipFile) {
    try {
      byte[] fileBytes = Files.readAllBytes(zipFile.toPath());
      String header =
          "--"
              + boundary
              + "\r\n"
              + "Content-Disposition: form-data; name=\"bundle\"; filename=\""
              + zipFile.getName()
              + "\"\r\n"
              + "Content-Type: application/zip\r\n\r\n";
      String footer = "\r\n--" + boundary + "--\r\n";

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      out.write(header.getBytes(StandardCharsets.UTF_8));
      out.write(fileBytes);
      out.write(footer.getBytes(StandardCharsets.UTF_8));
      return out.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
