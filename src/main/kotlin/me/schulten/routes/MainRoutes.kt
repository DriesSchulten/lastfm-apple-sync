package me.schulten.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import me.schulten.applemusic.AppleMusicClient
import me.schulten.sync.SyncService
import org.koin.ktor.ext.inject

fun Route.index() {
  get("/") {
    call.respond(MustacheContent("index.hbs", null))
  }
}

fun Route.sync() {
  val syncService by inject<SyncService>()

  get("/sync") {
    syncService.sync()
    call.respond(HttpStatusCode.NoContent)
  }
}

fun Route.static() {
  static("assets") {
    files("frontend/build")
  }
}

fun Application.registerMainRoutes() {
  routing {
    index()
    static()
    sync()
  }
}