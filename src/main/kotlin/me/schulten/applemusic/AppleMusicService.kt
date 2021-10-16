package me.schulten.applemusic

import io.ktor.http.URLBuilder

/**
 * Apple Music service
 *
 * @author dries
 */
interface AppleMusicService {

  /**
   * Tries to find an album with given name and artist name
   * @param name The albums name
   * @param artistName The artists name
   * @return The found [Album], if found
   */
  suspend fun findAlbum(name: String, artistName: String): Album?

  /**
   * Adds the given albums the users personal library
   * @param albums The Apple Music albums to add
   */
  suspend fun addAlbumsToLibrary(albums: List<Album>)
}

class AppleMusicServiceImpl(private val appleMusicClient: AppleMusicClient) : AppleMusicService {
  override suspend fun findAlbum(name: String, artistName: String): Album? {
    val term = "$name $artistName"

    tailrec suspend fun findAlbumInternal(term: String, offset: Int? = null): Album? {
      val results = appleMusicClient.searchAlbum(term, offset)

      val album = results.data.find { album ->
        album.attributes.name.equals(name, ignoreCase = true)
          && album.attributes.artistName.equals(artistName, ignoreCase = true)
      }

      return if (album != null) {
        album
      } else if (results.next != null) {
        val nextOffset = URLBuilder(results.next).parameters["offset"]!!.toInt()
        findAlbumInternal(term, nextOffset)
      } else {
        null
      }
    }

    return findAlbumInternal(term)
  }

  override suspend fun addAlbumsToLibrary(albums: List<Album>) {
    albums.map { it.id }.chunked(10).forEach { group ->
      appleMusicClient.addAlbumsToLibrary(group)
    }
  }
}