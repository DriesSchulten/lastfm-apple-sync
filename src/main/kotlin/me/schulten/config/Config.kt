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
import org.koin.ktor.ext.KoinApplicationStarted
import org.koin.ktor.ext.KoinApplicationStopPreparing
import org.koin.ktor.ext.get
import org.koin.logger.slf4jLogger
import org.quartz.Scheduler

fun Application.config() {
  environment.monitor.subscribe(KoinApplicationStarted) {
    get<Scheduler>().start()
  }

  configureDI()
  configureSerialization()
  configureTemplating()

  registerMainRoutes()
  registerAppleMusicAuthRoutes()

  environment.monitor.subscribe(KoinApplicationStopPreparing) {
    get<Scheduler>().shutdown()
  }
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
      val settings = ConfigLoader().loadConfigOrThrow<AppSettings>("/application.conf")

      logger.info("Settings: $settings")

      settings
    }
  }


  install(Koin) {
    slf4jLogger()
    modules(applicationModule, appleMusicModule, lastFmModule, syncModule)
  }
}