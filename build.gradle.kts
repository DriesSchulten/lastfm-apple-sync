val ktorVersion: String = "1.6.4"
val koinVersion: String = "3.1.2"
val logbackVersion: String = "1.2.6"
val kotlinVersion: String = "1.5.31"

plugins {
  kotlin("jvm") version "1.5.31"
  kotlin("plugin.serialization") version "1.5.31"
  id("me.qoomon.git-versioning") version "5.1.1"

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

sourceSets {
  main {
    resources {
      srcDir("build/resources/generated")
    }
  }
}

group = "me.schulten"
version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
  refs {
    branch(".+") {
      version = "\${ref}-SNAPSHOT"
    }
    tag("v(?<version>.*)") {
      version = "\${ref.version}"
    }
  }
  rev {
    version = "\${commit}"
  }
}

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

  implementation("org.quartz-scheduler:quartz:2.3.2")

  implementation("com.auth0:java-jwt:3.18.2")

  implementation("com.sksamuel.hoplite:hoplite-hocon:1.4.9")

  implementation("ch.qos.logback:logback-classic:$logbackVersion")
  implementation("io.github.microutils:kotlin-logging:2.0.11")

  testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
  testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
  testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
  testImplementation("io.mockk:mockk:1.12.0")
}

application {
  mainClass.set("me.schulten.ApplicationKt")
}