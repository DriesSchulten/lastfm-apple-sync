package me.schulten.lastfm

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.schulten.config.*
import org.junit.Assert
import org.junit.Test

/**
 * Last.fm service tests
 *
 * @author dries
 */
class LastFmServiceTest {

  private val appSettings = AppSettings(
    LastFm("https://last-fm-api/", "api-key", "shared-secret", "", "user", Period.WEEK),
    AppleMusic("", "", "", ""),
    Sync(1, ""),
    Storage("")
  )

  @Test
  fun getTopAlbumsTest() = runBlocking {
    val client = mockk<LastFmClient>()
    val service = LastFmServiceImpl(appSettings, client)

    coEvery { client.getTopAlbums(appSettings.lastFm.user, appSettings.lastFm.topAlbumPeriod) } returns listOf(
      Album("1", "Album", 2, Artist("1", "Artist"))
    )

    Assert.assertEquals(1, service.getTopAlbums().size)
  }

}