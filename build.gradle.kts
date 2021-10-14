val ktorVersion: String = "1.6.4"

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

group = "me.schulten"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.sksamuel.hoplite:hoplite-hocon:1.4.9")

  implementation("io.ktor:ktor-client-cio:$ktorVersion")
  implementation("io.ktor:ktor-client-serialization:$ktorVersion")

  implementation("com.github.ajalt.clikt:clikt:3.3.0")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnit()
}

application {
  mainClass.set("me.schulten.SyncClientKt")
}