import com.github.burrunan.s3cache.AwsS3BuildCache
import com.stano.gradle_dependency_management.MavenRepositoryUtil

rootProject.name = "gradle-plugins"

buildscript {
  val properties = getExtensions().extraProperties.properties
  val stanoMavenUrl = properties["com.stano.maven.url"]?.toString() ?: System.getenv("STANO_MAVEN_URL")
  val stanoMavenUsername = properties["com.stano.maven.username"]?.toString() ?: System.getenv("STANO_MAVEN_USERNAME")
  val stanoMavenPassword = properties["com.stano.maven.password"]?.toString() ?: System.getenv("STANO_MAVEN_PASSWORD")

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "stano-maven"
      url = uri(stanoMavenUrl)
      credentials {
        username = stanoMavenUsername
        password = stanoMavenPassword
      }
    }
  }
  dependencies {
    classpath("com.stano:gradle-dependency-management:${properties["gradleDependencyManagementVersion"].toString()}")
    classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:${properties["sonarPluginVersion"]}")
    classpath("com.github.burrunan.s3-build-cache:com.github.burrunan.s3-build-cache.gradle.plugin:1.9.5")
  }
}

MavenRepositoryUtil.configureDependencyResolutionManagement(extensions, dependencyResolutionManagement)

include("gradle-plugins-application")
include("gradle-plugins-bom")
include("gradle-plugins-docker")
include("gradle-plugins-java-common")
include("gradle-plugins-java-library")
include("gradle-plugins-java-module")
include("gradle-plugins-project")
include("gradle-plugins-settings")
include("gradle-plugins-sonar")
include("gradle-plugins-spring-boot")
include("gradle-plugins-test")
include("gradle-plugins-util")

apply(plugin = "com.github.burrunan.s3-build-cache")

gradle.settingsEvaluated {
  val properties = extensions.extraProperties.properties
  val s3Bucket = properties["com.stano.build-cache.s3.bucket"]?.toString() ?: System.getenv("STANO_BUILD_CACHE_S3_BUCKET")
  val s3Region = properties["com.stano.build-cache.s3.region"]?.toString() ?: System.getenv("STANO_BUILD_CACHE_S3_REGION")
  val s3AccessKeyId = properties["com.stano.build-cache.s3.access-key-id"]?.toString() ?: System.getenv("STANO_BUILD_CACHE_S3_ACCESS_KEY_ID")
  val s3SecretAccessKey = properties["com.stano.build-cache.s3.secret-access-key"]?.toString() ?: System.getenv("STANO_BUILD_CACHE_S3_SECRET_ACCESS_KEY")

  buildCache {
    local {
      isEnabled = true
    }

    if (s3Bucket != null && s3Region != null) {
      remote<AwsS3BuildCache> {
        bucket = s3Bucket
        region = s3Region
        awsAccessKeyId = s3AccessKeyId
        awsSecretKey = s3SecretAccessKey
        prefix = "gradle-plugins/"
        isPush = properties.getOrDefault("com.stano.build-cache.push-enabled", "false").toString().toBoolean()
      }
    }
  }
}
