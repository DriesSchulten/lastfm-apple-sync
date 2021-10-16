val ktorVersion: String = "1.6.4"
val koinVersion: String = "3.1.2"
val logbackVersion: String = "1.2.6"
val kotlinVersion: String = "1.5.31"

plugins {
  kotlin("jvm") version "1.5.31"
  kotlin("plugin.serialization") version "1.5.31"
  application
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
}

group = "me.schulten"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-serialization:$ktorVersion")
  implementation("io.ktor:ktor-mustache:$ktorVersion")

  implementation("io.insert-koin:koin-core:$koinVersion")
  implementation("io.insert-koin:koin-ktor:$koinVersion")
  implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

  implementation("io.ktor:ktor-client-cio:$ktorVersion")
  implementation("io.ktor:ktor-client-serialization:$ktorVersion")
  implementation("io.ktor:ktor-client-logging:$ktorVersion")

  implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")

  implementation("com.auth0:java-jwt:3.18.2")

  implementation("com.sksamuel.hoplite:hoplite-hocon:1.4.9")

  implementation("ch.qos.logback:logback-classic:$logbackVersion")
  implementation("io.github.microutils:kotlin-logging:2.0.11")

  testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
  testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

application {
  mainClass.set("me.schulten.ApplicationKt")
}