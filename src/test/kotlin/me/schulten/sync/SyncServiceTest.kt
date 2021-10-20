package me.schulten.sync

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.schulten.applemusic.AlbumAttributes
import me.schulten.applemusic.AppleMusicService
import me.schulten.applemusic.ExpiredUserCredentialsException
import me.schulten.config.*
import me.schulten.lastfm.Album
import me.schulten.lastfm.Artist
import me.schulten.lastfm.LastFmService
import me.schulten.lastfm.Period
import org.junit.Assert
import org.junit.Test

/**
 * Tests sync service
 *
 * @author dries
 */
class SyncServiceTest {

  private val appSettings = AppSettings(
    LastFm("", "", "", "", "", Period.WEEK),
    AppleMusic("", "", "", ""),
    Sync(2, ""),
    Storage("")
  )

  @Test
  fun syncTest() = runBlocking {
    val lastFmService = mockk<LastFmService>()
    val appleMusicService = mockk<AppleMusicService>()

    // 3 albums, increasing play count (so 1, 2 and 3)
    coEvery { lastFmService.getTopAlbums() } returns (1 until 4).map { idx ->
      Album("$idx", "Album $idx", idx, Artist("$idx", "Artist $idx"))
    }

    // This album is expected to be added by the sync (play-count of 2 and found in Apple Music)
    val expectedAlbumToAdd = me.schulten.applemusic.Album(
      "2",
      "",
      AlbumAttributes("Album 2", "Artist 2", isSingle = false)
    )

    // Apple Music responses, #1 and #2 found
    coEvery { appleMusicService.findAlbum("Album 1", "Artist 1") } returns me.schulten.applemusic.Album(
      "1",
      "",
      AlbumAttributes("Album 1", "Artist 1", isSingle = false)
    )
    coEvery { appleMusicService.findAlbum("Album 2", "Artist 2") } returns expectedAlbumToAdd

    // #3 not found
    coEvery { appleMusicService.findAlbum("Album 3", "Artist 3") } returns null

    // Accept all tries to add albums to lib
    coJustRun { appleMusicService.addAlbumsToLibrary(any()) }

    val service = SyncServiceImpl(appSettings, lastFmService, appleMusicService)
    service.sync()

    // After run we check the status
    val status = service.currentSync

    // Check if expected album is added to lib
    coVerify {
      appleMusicService.addAlbumsToLibrary(listOf(expectedAlbumToAdd))
    }

    // Status should reflect what happened
    Assert.assertNotNull(status)
    Assert.assertEquals(2, status!!.numberOfAlbums)
    Assert.assertEquals(1, status.notFound.size)
    Assert.assertEquals("Album 3", status.notFound[0].name)
    Assert.assertNull(status.error)
  }

  @Test
  fun syncErrorTest() = runBlocking {
    val lastFmService = mockk<LastFmService>()
    val appleMusicService = mockk<AppleMusicService>()

    coEvery { lastFmService.getTopAlbums() } returns listOf(
      Album("1", "Album 1", 2, Artist("1", "Artist 1"))
    )

    coEvery { appleMusicService.findAlbum(any(), any()) } throws ExpiredUserCredentialsException

    val service = SyncServiceImpl(appSettings, lastFmService, appleMusicService)
    service.sync()

    val status = service.currentSync
    Assert.assertNotNull(status?.error)
  }

}