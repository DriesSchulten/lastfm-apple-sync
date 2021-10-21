package me.schulten.routes

import com.auth0.jwt.exceptions.JWTCreationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.MustacheContent
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import me.schulten.applemusic.AppleMusicCredentialHelper
import me.schulten.applemusic.UserToken
import me.schulten.routes.viewmodels.AppleMusicUserAuthViewModel
import org.koin.ktor.ext.inject

fun Route.appleMusicUserAuth() {
  val credentialHelper by inject<AppleMusicCredentialHelper>()

  get("/apple-music") {
    try {
      val token = credentialHelper.developerToken
      val viewModel = AppleMusicUserAuthViewModel(token)
      call.respond(MustacheContent("apple-music-user-auth.hbs", mapOf("model" to viewModel)))
    } catch (e: Exception) {
      call.application.log.error("Error getting/creating Apple Music developer token", e)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }
}

fun Route.registerAppleMusicUserToken() {
  val credentialHelper by inject<AppleMusicCredentialHelper>()

  post("/apple-music/user-token") {
    val userToken = call.receive<UserToken>()
    credentialHelper.updateUserToken(userToken)
    call.respond(HttpStatusCode.NoContent)
  }
}

fun Route.deleteAppleMusicUserToken() {
  val credentialHelper by inject<AppleMusicCredentialHelper>()

  delete("/apple-music/user-token") {
    credentialHelper.deleteUserToken()
    call.respond(HttpStatusCode.NoContent)
  }
}

fun Application.registerAppleMusicAuthRoutes() {
  routing {
    appleMusicUserAuth()
    registerAppleMusicUserToken()
    deleteAppleMusicUserToken()
  }
}