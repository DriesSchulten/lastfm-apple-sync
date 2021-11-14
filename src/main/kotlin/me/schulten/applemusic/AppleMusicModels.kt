@file:UseSerializers(LocalDateTimeSerializer::class)

package me.schulten.applemusic

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.schulten.util.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class SearchResponse(val results: SearchResults)

@Serializable
data class SearchResults(val albums: ResultContainer<Album>? = null)

@Serializable
data class ResultContainer<E>(val data: List<E>, val href: String, val next: String? = null)

@Serializable
data class Album(val id: String, val href: String, val attributes: AlbumAttributes)

@Serializable
data class AlbumAttributes(val name: String, val artistName: String, val isSingle: Boolean)

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

abstract class AppleMusicException(message: String): Exception(message)

object MissingUserCredentialsException: AppleMusicException("Missing Apple Music user credentails, please authorize.")

object ExpiredUserCredentialsException: AppleMusicException("Expired Apple Music user credentails, please re-authorize.")

object MissingDeveloperCredentialsException: AppleMusicException("Missing Apple Music developer credentials")

object ApiRateLimitException: AppleMusicException("Hit the Apple Music API rate limit")