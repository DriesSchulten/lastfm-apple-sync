package me.schulten.applemusic

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.schulten.config.*
import me.schulten.lastfm.Period
import org.junit.Assert
import org.junit.Test

/**
 * Apple Music API client tests
 *
 * @author dries
 */
class AppleMusicClientTest {

  private val appSettings = AppSettings(
    LastFm("", "", "", "", "", Period.WEEK),
    AppleMusic("", "", "", ""),
    Sync(1, ""),
    Storage("")
  )

  @Test
  fun searchAlbumTest() = runBlocking {
    val artist = "artist"
    val album = "album"

    val response = SearchResponse(
      SearchResults(
        ResultContainer(
          listOf(
            Album(
              "123", "<href>", AlbumAttributes(
                album, artist, false
              )
            )
          ), "<href>"
        )
      )
    )

    val developToken = "developer-token"

    val mockEngine = MockEngine { request ->
      Assert.assertEquals("Bearer $developToken", request.headers[HttpHeaders.Authorization])
      Assert.assertEquals("0", request.url.parameters["offset"])
      Assert.assertEquals("$artist $album", request.url.parameters["term"])
      Assert.assertEquals("albums", request.url.parameters["types"])

      respond(
        content = ByteReadChannel(Json.encodeToString(response)),
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val credentialHelper = mockk<AppleMusicCredentialHelper>()
    every { credentialHelper.storefrontId } returns "nl"
    every { credentialHelper.developerToken } returns developToken

    val client = AppleMusicClientImpl(mockEngine, appSettings, credentialHelper)
    val result = client.searchAlbum("$artist $album", 0)

    Assert.assertEquals(response.results.albums.data, result.data)
  }

  @Test
  fun addAlbumsToLibraryTest() = runBlocking {
    val ids = listOf("a", "b", "c")
    val developToken = "developer-token"
    val userToken = "user-token"

    val mockEngine = MockEngine { request ->
      Assert.assertEquals("Bearer $developToken", request.headers[HttpHeaders.Authorization])
      Assert.assertEquals(userToken, request.headers["Music-User-Token"])
      Assert.assertEquals(ids.joinToString(","), request.url.parameters["ids[albums]"])

      respond(
        content = "",
        status = HttpStatusCode.NoContent
      )
    }

    val credentialHelper = mockk<AppleMusicCredentialHelper>()
    every { credentialHelper.developerToken } returns developToken
    every { credentialHelper.userToken } returns userToken

    val client = AppleMusicClientImpl(mockEngine, appSettings, credentialHelper)
    client.addAlbumsToLibrary(ids)
  }

  @Test(expected = ApiRateLimitException::class)
  fun handleRateLimitErrorTest() = runBlocking {
    val mockEngine = MockEngine {
      respond(
        content = "",
        status = HttpStatusCode.TooManyRequests
      )
    }

    val credentialHelper = mockk<AppleMusicCredentialHelper>()
    every { credentialHelper.developerToken } returns "developer-token"
    every { credentialHelper.userToken } returns "user-token"

    val client = AppleMusicClientImpl(mockEngine, appSettings, credentialHelper)
    client.addAlbumsToLibrary(emptyList())
  }
}