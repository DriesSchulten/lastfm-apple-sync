package me.schulten.config

import com.github.mustachejava.DefaultMustacheFactory
import com.sksamuel.hoplite.ConfigLoader
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.mustache.Mustache
import io.ktor.serialization.json
import me.schulten.applemusic.appleMusicModule
import me.schulten.lastfm.lastFmModule
import me.schulten.routes.registerAppleMusicAuthRoutes
import me.schulten.routes.registerMainRoutes
import me.schulten.sync.syncModule
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun Application.config() {
  configureDI()
  configureSerialization()
  configureTemplating()

  registerMainRoutes()
  registerAppleMusicAuthRoutes()
}

fun Application.configureSerialization() {
  install(ContentNegotiation) {
    json()
  }
}

fun Application.configureTemplating() {
  install(Mustache) {
    mustacheFactory = DefaultMustacheFactory("templates")
  }
}

fun Application.configureDI() {
  val applicationModule = module {
    single {
      ConfigLoader().loadConfigOrThrow<AppSettings>("/application.conf")
    }
  }


  install(Koin) {
    slf4jLogger()
    modules(applicationModule, appleMusicModule, lastFmModule, syncModule)
  }
}