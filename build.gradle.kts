import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.micronaut.gradle.MicronautRuntime
import io.micronaut.gradle.MicronautTestRuntime
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17 as JVMLevel

plugins {
  application
  java
  id("org.jetbrains.kotlin.jvm") version "1.9.20-Beta2"
  id("org.jetbrains.kotlin.plugin.allopen") version "1.9.20-Beta2"
  id("com.google.devtools.ksp") version "1.9.20-Beta2-1.0.13"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("org.graalvm.buildtools.native") version "0.9.27"
  id("io.micronaut.application") version "4.1.1"
  id("io.micronaut.graalvm") version "4.1.1"
  id("io.micronaut.docker") version "4.1.1"
  id("io.micronaut.aot") version "4.1.1"
}

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("com.guardsquare:proguard-gradle:7.4.0-beta02")
  }
}

version = "0.1"
group = "com.example"

val enableJpms = false
val kotlinVersion: String by properties
val graalvmVersion: String by properties
val jvmTarget: String by properties
val entrypoint = "com.example.DemoCommand"
val moduleId = "sample"

val jvmCompileArgs = listOf(
  "--enable-preview",
  "--add-modules=jdk.incubator.vector",
//  "--add-opens=java.base/java.io=ALL-UNNAMED",
//  "--add-opens=java.base/java.nio=ALL-UNNAMED",
//  "--add-exports=org.graalvm.truffle/com.oracle.truffle.object=ALL-UNNAMED",
//  "--add-exports=org.graalvm.truffle.runtime/com.oracle.truffle.runtime=ALL-UNNAMED",
//  "--add-exports=jdk.internal.vm.compiler/org.graalvm.compiler.options=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.jdk=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.hosted=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.hosted.c=ALL-UNNAMED",
//  "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.base/com.oracle.svm.util=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.option=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.jdk=ALL-UNNAMED",
//  "--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.jni=ALL-UNNAMED",
)

val ktCompilerArgs = listOf(
  "-Xallow-unstable-dependencies",
  "-Xcontext-receivers",
  "-Xemit-jvm-type-annotations",
  "-Xlambdas=indy",
  "-Xsam-conversions=indy",
  "-Xjsr305=strict",
  "-Xjvm-default=all",
  "-Xjavac-arguments=${jvmCompileArgs.joinToString(",")}}",
)

val commonNativeArgs = listOf(
  "-H:+UnlockExperimentalVMOptions",
  "--enable-native-access=" + listOfNotNull(
    "org.graalvm.polyglot",
    "org.graalvm.js",
    "ALL-UNNAMED",
  ).joinToString(","),
)

val nativeDebugFlags = emptyList<String>()

val nativeReleaseFlags = listOf(
  "-O3",
)

val initializeAtBuildTime = listOf(
  "kotlin.coroutines.intrinsics.CoroutineSingletons",
)

val initializeAtRunTime = emptyList<String>()

fun nativeImageArgs(
  debug: Boolean = false,
  release: Boolean = true,
  libc: String = "glibc",
  static: Boolean = false,
): List<String> {
  return commonNativeArgs.plus(listOfNotNull(
    "--libc=$libc",
    if (static) "--static" else null,
  )).plus(
    if (release) nativeReleaseFlags else emptyList()
  ).plus(
    if (debug) nativeDebugFlags else emptyList()
  ).plus(
    initializeAtBuildTime.map { "--initialize-at-build-time=$it" }
  ).plus(
    initializeAtRunTime.map { "--initialize-at-run-time=$it" }
  ).plus(
    jvmCompileArgs.map { "-J$it" }
  )
}

val r8: Configuration by configurations.creating

dependencies {
  r8(libs.r8)
  ksp(mn.micronaut.inject.kotlin)
  ksp(mn.picocli.codegen)
  implementation(mn.picocli)
  implementation(mn.micronaut.core)
  implementation(mn.micronaut.kotlin.runtime)
  implementation(mn.micronaut.picocli)
  implementation(kotlin("stdlib-jdk8"))
  runtimeOnly(mn.snakeyaml)

  // add svm dependency as compile only
  compileOnly(libs.graalvm.svm)
  implementation(libs.bundles.graalvm)
}

application {
  mainClass = entrypoint
//  mainModule = moduleId
}

java {
  sourceCompatibility = JavaVersion.toVersion(jvmTarget)
  targetCompatibility = JavaVersion.toVersion(jvmTarget)

  if (enableJpms) modularity.inferModulePath = true

  disableAutoTargetJvm()
  consistentResolution {
    useCompileClasspathVersions()
  }
}

tasks {
  compileJava {
    if (enableJpms) modularity.inferModulePath = true
  }

  compileKotlin {
    compilerOptions {
      jvmTarget = JVMLevel
      freeCompilerArgs = freeCompilerArgs.get().plus(ktCompilerArgs)
    }
  }
  compileTestKotlin {
    compilerOptions {
      jvmTarget = JVMLevel
      freeCompilerArgs = freeCompilerArgs.get().plus(ktCompilerArgs)
    }
  }
}

micronaut {
  runtime = MicronautRuntime.NONE
  testRuntime = MicronautTestRuntime.JUNIT_5
  enableNativeImage(true)

  processing {
    incremental(true)
    annotations("com.example.*")
  }

  aot {
    convertYamlToJava = false
    precomputeOperations = false
    cacheEnvironment = false
    deduceEnvironment = false
    replaceLogbackXml = false

    optimizeServiceLoading = false
    optimizeClassLoading = false
    optimizeNetty = false
    possibleEnvironments = listOf("cli")

    netty {
      enabled = false
      machineId = "13-37-7C-D1-6F-F5"
      pid = "1337"
    }
  }
}

tasks.jar {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.optimizedNativeJar {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

graalvmNative {
  toolchainDetection = false
  testSupport = true

  agent {
    defaultMode = "standard"
    builtinCallerFilter = true
    builtinHeuristicFilter = true
    enableExperimentalPredefinedClasses = false
    enableExperimentalUnsafeAllocationTracing = false
    trackReflectionMetadata = true
    enabled = System.getenv("GRAALVM_AGENT") == "true"

    modes {
      standard {}
    }
    metadataCopy {
      inputTaskNames.addAll(listOf("run", "optimizedRun"))
      outputDirectories.add("src/main/resources/META-INF/native-image")
      mergeWithExisting = true
    }
  }

  binaries {
    named("main") {
      fallback = false
      quickBuild = true
      imageName = "sample.debug"
      buildArgs.addAll(nativeImageArgs(debug = true))
    }

    named("optimized") {
      fallback = false
      quickBuild = false
      imageName = "sample"
      buildArgs.addAll(nativeImageArgs(release = true))
    }
  }
}
