package me.schulten.routes

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import me.schulten.config.configureSerialization
import me.schulten.config.configureTemplating
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin

/**
 * Default setup for testing the API (like JSON/templates etc)
 *
 * @author dries
 */
abstract class AbstractRoutesTest {

  protected abstract val koinModule: Module
  protected abstract val registerRoutes: Application.() -> Unit

  fun withSyncTestApplication(test: TestApplicationEngine.() -> Unit) {
    withTestApplication({
      configureSerialization()
      configureTemplating()

      install(Koin) {
        modules(koinModule)
      }

      registerRoutes()
    }) {
      test()
    }
  }

}