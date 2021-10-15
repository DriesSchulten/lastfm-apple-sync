package me.schulten.lastfm

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLBuilder
import me.schulten.config.AppSettings
import java.security.MessageDigest

/**
 * Last.fm related API functions
 */
interface LastFmClient {
  /**
   * Fetches top albums for a given user id, handles paging internally (i.e. fetches all pages as 1 list)
   * @param user The Last.fm username
   * @param period The desired period
   * @return The users top albums
   */
  suspend fun getTopAlbums(user: String, period: Period = Period.WEEK): List<Album>
}

class LastFmClientImpl(private val appSettings: AppSettings) : LastFmClient {

  private val httpClient = HttpClient(CIO) {
    install(JsonFeature) {
      serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        isLenient = true
      })
    }
  }

  private val lastFm = appSettings.lastFm

  suspend fun getToken(): String {
    val token: GetToken = httpClient.get(lastFm.baseUrl) {
      lastFmParameters("auth.gettoken", lastFm.apiKey)
      lastFmSignRequest()
    }

    return token.token
  }

  fun getAuthUrl(token: String): String {
    val authUrl = URLBuilder(appSettings.lastFm.authUrl)
    authUrl.parameters.append("api_key", lastFm.apiKey)
    authUrl.parameters.append("token", token)
    return authUrl.buildString()
  }

  suspend fun getSession(token: String): String {
    val session: GetSession = httpClient.get(lastFm.baseUrl) {
      lastFmParameters("auth.getSession", lastFm.apiKey)
      parameter("token", token)
      lastFmSignRequest()
    }

    return session.key
  }

  override suspend fun getTopAlbums(user: String, period: Period): List<Album> {
    tailrec suspend fun getTopAlbums(
      page: Int,
      results: List<Album>,
      user: String,
      period: Period = Period.WEEK
    ): List<Album> {
      val topAlbums: TopAlbums = httpClient.get<TopAlbumsResult>(lastFm.baseUrl) {
        parameter("page", page)
        parameter("user", user)
        parameter("period", period.argName)
        lastFmParameters("user.gettopalbums", lastFm.apiKey)
      }.topAlbums

      val attribs = topAlbums.attributes

      return if (attribs.page < attribs.totalPages) {
        getTopAlbums(attribs.page + 1, results + topAlbums.albums, user, period)
      } else {
        results + topAlbums.albums
      }
    }


    return getTopAlbums(1, emptyList(), user, period)
  }
}

/**
 * Adds LastFM required/optional default parameters
 */
fun HttpRequestBuilder.lastFmParameters(method: String, apiKey: String, sessionKey: String? = null) {
  parameter("format", "json")
  parameter("api_key", apiKey)
  parameter("method", method)
  parameter("sk", sessionKey)
}

/**
 * Sign request as per https://www.last.fm/api/authspec#_8-signing-calls
 */
fun HttpRequestBuilder.lastFmSignRequest() {
  val sigInput = url.parameters
    .names()
    .filter { !nonSigKeys.contains(it) }
    .sorted()
    .joinToString("") { it + url.parameters[it] }

  val md5 = MessageDigest.getInstance("MD5")
  val bytes = md5.digest(sigInput.toByteArray())

  parameter("api_sig", bytes.joinToString("") { byte -> "%02x".format(byte) })
}

private val nonSigKeys = setOf("format", "callback")