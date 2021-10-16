package me.schulten.lastfm

import com.github.benmanes.caffeine.cache.Caffeine
import me.schulten.config.AppSettings
import me.schulten.util.SuspendingCache
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Last.fm service
 *
 * @author dries
 */
interface LastFmService {

  /**
   * Gets top albums, cached
   * @return A list of [Album]s that are in the users top
   */
  suspend fun getTopAlbums(): List<Album>
}

class LastFmServiceImpl(private val appSettings: AppSettings, private val lastFmClient: LastFmClient) : LastFmService {

  private val cache: SuspendingCache<LocalDate, List<Album>> = SuspendingCache(
    Caffeine.newBuilder()
      .expireAfterWrite(2, TimeUnit.HOURS)
      .maximumSize(5)
      .buildAsync()
  )

  override suspend fun getTopAlbums(): List<Album> {
    val key = LocalDate.now()

    return cache.get(key) {
      val user = appSettings.lastFm.user
      val period = appSettings.lastFm.topAlbumPeriod

      lastFmClient.getTopAlbums(user, period)
    }
  }
}