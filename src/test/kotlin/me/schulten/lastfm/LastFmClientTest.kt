package me.schulten.lastfm

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.schulten.config.*
import org.junit.Assert
import org.junit.Test
import java.lang.Integer.min
import kotlin.math.ceil

/**
 * Tests for the Last.fm client
 *
 * @author dries
 */
class LastFmClientTest {
  private val appSettings = AppSettings(
    LastFm("https://last-fm-api/", "api-key", "shared-secret", "", "user", Period.WEEK),
    AppleMusic("", "", "", ""),
    Sync(1, ""),
    Storage("")
  )

  @Test
  fun getTopAlbumsTest() = runBlocking {
    val mockEngine = MockEngine { request ->
      Assert.assertEquals(appSettings.lastFm.apiKey, request.url.parameters["api_key"])
      Assert.assertEquals("json", request.url.parameters["format"])
      Assert.assertEquals("user.gettopalbums", request.url.parameters["method"])

      Assert.assertEquals("1", request.url.parameters["page"])
      Assert.assertEquals(appSettings.lastFm.user, request.url.parameters["user"])
      Assert.assertEquals(appSettings.lastFm.topAlbumPeriod.argName, request.url.parameters["period"])

      respond(
        content = ByteReadChannel(Json.encodeToString(topAlbumsResult())),
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val client = LastFmClientImpl(mockEngine, appSettings)
    val result = client.getTopAlbums(appSettings.lastFm.user, appSettings.lastFm.topAlbumPeriod)

    Assert.assertEquals(1, result.size)
    Assert.assertEquals("Album 0", result[0].name)
    Assert.assertEquals("Artist 0", result[0].artist.name)
  }

  @Test
  fun getTopAlbumsPagedTest() = runBlocking {
    val mockEngine = MockEngine { request ->
      val page = request.url.parameters["page"]!!.toInt()

      respond(
        content = ByteReadChannel(Json.encodeToString(topAlbumsResult(page, 10, 25))),
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val client = LastFmClientImpl(mockEngine, appSettings)
    val result = client.getTopAlbums(appSettings.lastFm.user, appSettings.lastFm.topAlbumPeriod)

    Assert.assertEquals(25, result.size)
  }

  private fun topAlbumsResult(page: Int = 1, perPage: Int = 1, total: Int = 1) = TopAlbumsResult(
    topAlbums = TopAlbums(
      albums = ((page - 1) * perPage until min(perPage * page, total)).map { idx -> Album("$idx", "Album $idx", 1, Artist("$idx", "Artist $idx")) },
      attributes = ResultAttributes(ceil(total / perPage.toDouble()).toInt(), page, perPage, total)
    )
  )
}