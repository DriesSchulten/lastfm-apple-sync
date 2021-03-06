package me.schulten.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import me.schulten.sync.SyncJobScheduler
import me.schulten.sync.SyncService
import org.koin.ktor.ext.inject


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
  static("static") {
    resources("static")
  }
  static("/") {
    defaultResource("index.html")
  }
}

fun Application.registerMainRoutes() {
  routing {
    static()
    sync()
    syncStatus()
  }
}