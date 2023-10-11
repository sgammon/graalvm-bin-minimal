plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.20-Beta2"
  id("org.jetbrains.kotlin.plugin.allopen") version "1.9.20-Beta2"
  id("com.google.devtools.ksp") version "1.9.20-Beta2-1.0.13"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("io.micronaut.application") version "4.1.1"
  id("io.micronaut.graalvm") version "4.1.1"
  id("io.micronaut.docker") version "4.1.1"
  id("io.micronaut.aot") version "4.1.1"
}

version = "0.1"
group = "com.example"

val kotlinVersion: String by properties

dependencies {
  ksp("info.picocli:picocli-codegen")
  implementation("info.picocli:picocli")
  implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
  implementation("io.micronaut.picocli:micronaut-picocli")
  implementation("io.micronaut.serde:micronaut-serde-jackson")
  implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
  runtimeOnly("ch.qos.logback:logback-classic")
  runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
  runtimeOnly("org.yaml:snakeyaml")
}

application {
  mainClass = "com.example.DemoCommand"
}

java {
  sourceCompatibility = JavaVersion.toVersion("20")
}

tasks {
  compileKotlin {
    compilerOptions {
      jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_20
    }
  }
  compileTestKotlin {
    compilerOptions {
      jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_20
    }
  }
}

micronaut {
  testRuntime("junit5")
  processing {
    incremental(true)
    annotations("com.example.*")
  }
}
