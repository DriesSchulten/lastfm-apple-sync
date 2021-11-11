package me.schulten.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import me.schulten.applemusic.AppleMusicCredentialHelper
import me.schulten.applemusic.UserToken
import org.koin.ktor.ext.inject

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

fun Route.appleMusicDeveloperToken() {
  val credentialHelper by inject<AppleMusicCredentialHelper>()

  get("/apple-music/developer-token") {
    val token = credentialHelper.developerToken
    call.respond(mapOf("token" to token))
  }
}

fun Application.registerAppleMusicAuthRoutes() {
  routing {
    registerAppleMusicUserToken()
    deleteAppleMusicUserToken()
    appleMusicDeveloperToken()
  }
}