
import build.less.plugin.settings.buildless

pluginManagement {
  repositories {
    maven("https://gradle.pkg.st/")
    maven("https://maven.pkg.st/")
  }
}

plugins {
  id("build.less") version("1.0.0-beta1")
  id("com.gradle.enterprise") version("3.14.1")
  id("com.gradle.common-custom-user-data-gradle-plugin") version("1.11")
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

val elideVersion: String by settings
val micronautVersion: String by settings

dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.PREFER_PROJECT

  repositories {
    maven("https://maven.pkg.st/")
    maven("https://storage.googleapis.com/r8-releases/raw")
    google()
  }
  versionCatalogs {
    create("mn") {
      from("io.micronaut.platform:micronaut-platform:$micronautVersion")
    }
    create("framework") {
      from("dev.elide:elide-bom:$elideVersion")
    }
  }
}

buildless {
   // nothing to set
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
  
rootProject.name = "elide-bin-minimal"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("GROOVY_COMPILATION_AVOIDANCE")
