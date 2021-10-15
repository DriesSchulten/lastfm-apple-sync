package me.schulten.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing

fun Route.index() {
  get("/") {
    call.respond(MustacheContent("index.hbs", null))
  }
}

fun Application.registerMainRoutes() {
  routing {
    index()
  }
}