package me.schulten.applemusic

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

/**
 * Tests for the Apple Music service
 *
 * @author dries
 */
class AppleMusicServiceTest {

  private val client = mockk<AppleMusicClient>()

  @Test
  fun findAlbumTest() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns resultContainer(artist, album)

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    Assert.assertNotNull(result)
  }

  @Test
  fun findAlbumPagedTest() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns resultContainer("$artist not correct", album, "https://url?offset=1")
    coEvery { client.searchAlbum("$album $artist", 1) } returns resultContainer("$artist als not correct", album, "https://url?offset=2")
    coEvery { client.searchAlbum("$album $artist", 2) } returns resultContainer(artist, album)

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    Assert.assertNotNull(result)
  }

  @Test
  fun findAlbumNotFoundTest() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns ResultContainer(
      data = emptyList(),
      href = ""
    )

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    Assert.assertNull(result)
  }

  @Test
  fun addAlbumToLibraryTest() = runBlocking {
    val albums = (0 until 20).map { idx -> Album("$idx", "https://link/$idx", AlbumAttributes("Album $idx", "Artist $idx", isSingle = false)) }

    coJustRun {
      client.addAlbumsToLibrary(any())
    }

    val service = AppleMusicServiceImpl(client)
    service.addAlbumsToLibrary(albums)

    // Check if split in groups of 10
    coVerify {
      client.addAlbumsToLibrary(albums.subList(0, 10).map { it.id })
      client.addAlbumsToLibrary(albums.subList(10, 20).map { it.id })
    }
  }

  private fun resultContainer(artist: String, album: String, next: String? = null): ResultContainer<Album> = ResultContainer(
    data = listOf(
      Album(
        "", "", AlbumAttributes(
          name = album, artistName = artist, isSingle = false
        )
      )
    ), "", next
  )
}