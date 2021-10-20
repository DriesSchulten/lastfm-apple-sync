package me.schulten.lastfm

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import me.schulten.config.AppSettings

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

class LastFmClientImpl(engine: HttpClientEngine, appSettings: AppSettings) : LastFmClient {

  private val httpClient = HttpClient(engine) {
    install(JsonFeature) {
      serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        isLenient = true
      })
    }
  }

  private val lastFm = appSettings.lastFm

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
fun HttpRequestBuilder.lastFmParameters(method: String, apiKey: String) {
  parameter("format", "json")
  parameter("api_key", apiKey)
  parameter("method", method)
}