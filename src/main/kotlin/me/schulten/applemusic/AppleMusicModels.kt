@file:UseSerializers(LocalDateTimeSerializer::class)

package me.schulten.applemusic

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.schulten.util.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class AppleMusicTokens(
  val developerToken: String,
  val developerTokenExpires: LocalDateTime,
  val userToken: String? = null,
  val userTokenExpires: LocalDateTime? = null,
  val storefrontId: String? = null
)

@Serializable
data class UserToken(
  val token: String,
  val storefrontId: String
)

data class DeveloperToken(
  val token: String,
  val expires: LocalDateTime
)

object MissingUserCredentialsException: Exception("Missing Apple Music user credentails, please authorize.")

object ExpiredUserCredentialsException: Exception("Expired Apple Music user credentails, please re-authorize.")

object MissingDeveloperCredentialsException: Exception("Missing Apple Music developer credentials")