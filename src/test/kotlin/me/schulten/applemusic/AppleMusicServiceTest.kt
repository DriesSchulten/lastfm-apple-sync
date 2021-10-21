package me.schulten.applemusic

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests for the Apple Music service
 *
 * @author dries
 */
class AppleMusicServiceTest {

  private val client = mockk<AppleMusicClient>()

  @Test
  fun `find album should return an album if found`() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns resultContainer(artist, album)

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    assertNotNull(result)
  }

  @Test
  fun `find album should traverse pages uuntil result found and return the result`() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns resultContainer("$artist not correct", album, "https://url?offset=1")
    coEvery { client.searchAlbum("$album $artist", 1) } returns resultContainer("$artist als not correct", album, "https://url?offset=2")
    coEvery { client.searchAlbum("$album $artist", 2) } returns resultContainer(artist, album)

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    assertNotNull(result)
  }

  @Test
  fun `find album should return null when not found`() = runBlocking {
    val artist = "artist"
    val album = "album"

    coEvery { client.searchAlbum("$album $artist", null) } returns ResultContainer(
      data = emptyList(),
      href = ""
    )

    val service = AppleMusicServiceImpl(client)
    val result = service.findAlbum(album, artist)

    assertNull(result)
  }

  @Test
  fun `add library to album should split the albums to up into sub-lists and call the API for each sub-list`() = runBlocking {
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