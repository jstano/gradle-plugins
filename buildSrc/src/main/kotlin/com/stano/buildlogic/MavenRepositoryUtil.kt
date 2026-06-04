package com.stano.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.initialization.resolve.DependencyResolutionManagement
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.jvm.tasks.Jar

object MavenRepositoryUtil {
   private const val STANO_MAVEN_URL_PROPERTY = "com.stano.maven.url"
   private const val STANO_MAVEN_URL_ENVIRONMENT = "STANO_MAVEN_URL"

   fun configureDependencyResolutionManagement(
      extensionContainer: ExtensionContainer,
      dependencyResolutionManagement: DependencyResolutionManagement
   ) {
      dependencyResolutionManagement.repositories {
         mavenLocal()
         mavenCentral()
         gradlePluginPortal()
         MavenRepositoryUtil.createR365MavenRepository(extensionContainer, this)
      }
   }

   fun createR365MavenRepository(extensionContainer: ExtensionContainer, repositoryHandler: RepositoryHandler) {
      val properties = extensionContainer.getExtraProperties().getProperties()
      val stanoMavenUrl = if (properties.containsKey(STANO_MAVEN_URL_PROPERTY))
         properties.get(STANO_MAVEN_URL_PROPERTY).toString()
      else
         System.getenv(STANO_MAVEN_URL_ENVIRONMENT)

      repositoryHandler.maven {
         setName("stano-maven")
         setUrl(stanoMavenUrl)
         MavenRepositoryUtil.setRepositoryAuthentication(extensionContainer, this)
      }
   }

   fun createR365MavenRepository(project: Project, repositoryHandler: RepositoryHandler) {
      createR365MavenRepository(project.getExtensions(), repositoryHandler)
   }

   fun configurePublishing(project: Project) {
      configurePublishingRepositories(project)

      val publishingExtension = project.getExtensions().findByType<PublishingExtension>(PublishingExtension::class.java)
      publishingExtension!!.publications {
         val jarTask = project.getTasks().findByName("jar") as Jar?
         create<MavenPublication>(
            jarTask!!.getArchiveBaseName().get(),
            MavenPublication::class.java) {
            from(project.getComponents().findByName("java"))
            setArtifactId(jarTask.getArchiveBaseName().get())
         }
      }
   }

   fun configurePublishingRepositories(project: Project) {
      val publishingExtension = project.getExtensions().findByType<PublishingExtension>(PublishingExtension::class.java)
      publishingExtension!!.repositories {
         MavenRepositoryUtil.createR365MavenRepository(
            project,
            this
         )
      }
   }

   private fun setRepositoryAuthentication(extensionContainer: ExtensionContainer, repository: MavenArtifactRepository) {
      val properties = extensionContainer.getExtraProperties().getProperties()
      val stanoMavenUsername = if (properties.containsKey("com.stano.maven.username")) properties.get("com.stano.maven.username")
         .toString() else System.getenv("STANO_MAVEN_USERNAME")
      val stanoMavenPassword = if (properties.containsKey("com.stano.maven.password")) properties.get("com.stano.maven.password")
         .toString() else System.getenv("STANO_MAVEN_PASSWORD")

      val credentials = repository.getCredentials(PasswordCredentials::class.java)
      credentials.username = stanoMavenUsername
      credentials.password = stanoMavenPassword

      repository.authentication {
         create<HttpHeaderAuthentication>(
            "header",
            HttpHeaderAuthentication::class.java)
      }
   }
}
