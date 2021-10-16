package me.schulten.applemusic

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import me.schulten.config.AppSettings

/**
 * Apple Music API client
 *
 * @author dries
 */
interface AppleMusicClient {

  /**
   * Search for an album by name
   * @param name The albums name
   * @param offset The (optional) result offset (paging)
   * @return The [ResultContainer] containing found [Album]s
   */
  suspend fun searchAlbum(name: String, offset: Int? = null): ResultContainer<Album>
}

class AppleMusicClientImpl(private val appSettings: AppSettings, private val credentialHelper: AppleMusicCredentialHelper) : AppleMusicClient {

  private val appleMusic = appSettings.appleMusic

  private val httpClient = HttpClient(CIO) {
    install(JsonFeature) {
      serializer = KotlinxSerializer(Json {
        ignoreUnknownKeys = true
      })
    }
  }

  override suspend fun searchAlbum(name: String, offset: Int?): ResultContainer<Album> {
    val response = httpClient.get<SearchResponse>("${appleMusic.baseUrl}catalog/${credentialHelper.storefrontId}/search") {
      parameter("term", name)
      parameter("types", "albums")
      parameter("offset", offset)
      headers {
        append(HttpHeaders.Authorization, "Bearer ${credentialHelper.developerToken}")
      }
    }

    return response.results.albums
  }
}