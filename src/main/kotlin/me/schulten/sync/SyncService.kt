package me.schulten.sync

import me.schulten.applemusic.AppleMusicService
import me.schulten.config.AppSettings
import me.schulten.lastfm.LastFmService
import mu.KotlinLogging

/**
 * Runs actual sync
 *
 * @author dries
 */
interface SyncService {

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

  override suspend fun sync() {
    // Filtered albums
    val topAlbums = lastFmService.getTopAlbums()
      .filter { it.playcount >= appSettings.sync.minPlayCount }

    logger.info { "Found ${topAlbums.size} albums with play count >= ${appSettings.sync.minPlayCount} for period ${appSettings.lastFm.topAlbumPeriod.argName}" }

    val toSync = topAlbums.mapNotNull { lastFmAlbum ->
      val appleMusicAlbum = appleMusicService.findAlbum(lastFmAlbum.name, lastFmAlbum.artist.name)

      if (appleMusicAlbum == null) {
        logger.warn { "No Apple Music album found for album '${lastFmAlbum.name}' and artist '${lastFmAlbum.artist}'." }
      }

      appleMusicAlbum
    }

    toSync.forEach { album ->
      logger.info { "Adding album '${album.attributes.name}' from artist '${album.attributes.artistName}' to personal collection." }
    }

    appleMusicService.addAlbumsToLibrary(toSync)

    logger.info { "Done! Added ${toSync.size} albums to personal collection." }
  }
}