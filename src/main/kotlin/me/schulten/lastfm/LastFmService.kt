package me.schulten.lastfm

import me.schulten.config.AppSettings

/**
 * Last.fm service
 *
 * @author dries
 */
interface LastFmService {

  /**
   * Gets top albums
   * @return A list of [Album]s that are in the users top
   */
  suspend fun getTopAlbums(): List<Album>
}

class LastFmServiceImpl(private val appSettings: AppSettings, private val lastFmClient: LastFmClient) : LastFmService {

  override suspend fun getTopAlbums(): List<Album> {
    val user = appSettings.lastFm.user
    val period = appSettings.lastFm.topAlbumPeriod

    return lastFmClient.getTopAlbums(user, period)
  }
}