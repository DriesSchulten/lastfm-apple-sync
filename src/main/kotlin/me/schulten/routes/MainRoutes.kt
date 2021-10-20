package me.schulten.routes

import com.auth0.jwt.exceptions.JWTCreationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import me.schulten.applemusic.AppleMusicCredentialHelper
import me.schulten.routes.viewmodels.AppleMusicUserAuthViewModel
import me.schulten.sync.SyncAlbum
import me.schulten.sync.SyncJobScheduler
import me.schulten.sync.SyncService
import me.schulten.sync.SyncStatus
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

fun Route.index() {
  val credentialHelper by inject<AppleMusicCredentialHelper>()

  get("/") {
    try {
      val token = credentialHelper.developerToken
      val viewModel = AppleMusicUserAuthViewModel(token)
      call.respond(MustacheContent("index.hbs", mapOf("model" to viewModel)))
    } catch (e: JWTCreationException) {
      call.application.log.error("Error creating JWT for Apple Music", e)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }
}

fun Route.sync() {
  val syncService by inject<SyncService>()
  val syncJobScheduler by inject<SyncJobScheduler>()

  get("/sync") {
    val currentSync = syncService.currentSync

    if (currentSync == null || !currentSync.running) {
      syncJobScheduler.scheduleImmediately()
      call.respond(HttpStatusCode.NoContent)
    } else {
      call.respond(HttpStatusCode.BadRequest)
    }
  }
}

fun Route.syncStatus() {
  val syncService by inject<SyncService>()

  get("/sync-status") {
    val currentSync = syncService.currentSync
    if (currentSync != null) {
      call.respond(currentSync)
    } else {
      call.respond(HttpStatusCode.NotFound)
    }
  }
}

fun Route.static() {
  static("assets") {
    resources("frontend")
  }
}

fun Application.registerMainRoutes() {
  routing {
    index()
    static()
    sync()
    syncStatus()
  }
}