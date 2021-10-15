package me.schulten.routes

import com.auth0.jwt.exceptions.JWTCreationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import me.schulten.applemusic.AppleMusicTokenGenerator
import org.koin.ktor.ext.inject

fun Route.appleMusicDeveloperToken() {
  val tokenGenerator by inject<AppleMusicTokenGenerator>()

  get("/apple-music/developer-token") {
    try {
      val token = tokenGenerator.generateToken()
      call.respond(token)
    } catch (e: JWTCreationException) {
      call.application.log.error("Error creating JWT for Apple Music", e)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }
}

fun Route.appleMusicUserAuth() {

}

fun Application.registerAppleMusicAuthRoutes() {
  routing {
    appleMusicDeveloperToken()
  }
}