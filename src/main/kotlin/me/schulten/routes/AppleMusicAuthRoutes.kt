package me.schulten.routes

import com.auth0.jwt.exceptions.JWTCreationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import me.schulten.applemusic.AppleMusicTokenGenerator
import me.schulten.routes.viewmodels.AppleMusicUserAuthViewModel
import org.koin.ktor.ext.inject

fun Route.appleMusicUserAuth() {
  val tokenGenerator by inject<AppleMusicTokenGenerator>()

  get("/apple-music") {
    try {
      val token = tokenGenerator.generateToken()
      val viewModel = AppleMusicUserAuthViewModel(token)
      call.respond(MustacheContent("apple-music-user-auth.hbs", mapOf("model" to viewModel)))
    } catch (e: JWTCreationException) {
      call.application.log.error("Error creating JWT for Apple Music", e)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }
}

fun Application.registerAppleMusicAuthRoutes() {
  routing {
    appleMusicUserAuth()
  }
}