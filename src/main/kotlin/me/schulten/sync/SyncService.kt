package me.schulten.sync

import me.schulten.applemusic.AppleMusicService
import me.schulten.config.AppSettings
import me.schulten.lastfm.LastFmService
import mu.KotlinLogging
import java.time.LocalDateTime

/**
 * Runs actual sync
 *
 * @author dries
 */
interface SyncService {

  /**
   * Obtains current running sync status, if any
   */
  val currentSync: SyncStatus?

  /**
   * Runs sync as configured
   */
  suspend fun sync()
}

private val logger = KotlinLogging.logger {}

class SyncServiceImpl(
  private val appSettings: AppSettings,
  private val lastFmService: LastFmService,
  private val appleMusicService: AppleMusicService
) : SyncService {

  override var currentSync: SyncStatus? = null
    private set

  override suspend fun sync() {
    currentSync = SyncStatus(LocalDateTime.now(), 0, emptyList(), running = true)

    // Filtered albums
    val topAlbums = lastFmService.getTopAlbums()
      .filter { it.playcount >= appSettings.sync.minPlayCount }

    logger.info { "Found ${topAlbums.size} albums with play count >= ${appSettings.sync.minPlayCount} for period ${appSettings.lastFm.topAlbumPeriod.argName}" }
    currentSync = currentSync?.copy(numerOfAlbums = topAlbums.size)

    val toSync = topAlbums.mapNotNull { lastFmAlbum ->
      val appleMusicAlbum = appleMusicService.findAlbum(lastFmAlbum.name, lastFmAlbum.artist.name)

      if (appleMusicAlbum == null) {
        logger.warn { "No Apple Music album found for album '${lastFmAlbum.name}' and artist '${lastFmAlbum.artist}'." }
        currentSync = currentSync?.copy(notFound = (currentSync?.notFound ?: emptyList()) + SyncAlbum(lastFmAlbum.name, lastFmAlbum.artist.name))
      }

      appleMusicAlbum
    }

    toSync.forEach { album ->
      logger.info { "Adding album '${album.attributes.name}' from artist '${album.attributes.artistName}' to personal collection." }
    }

    appleMusicService.addAlbumsToLibrary(toSync)

    logger.info { "Done! Added ${toSync.size} albums to personal collection." }

    currentSync = currentSync?.copy(running = false)
  }
}