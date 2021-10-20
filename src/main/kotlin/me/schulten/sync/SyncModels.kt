@file:UseSerializers(LocalDateTimeSerializer::class)

package me.schulten.sync

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import me.schulten.util.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class SyncStatus(
  val startedAt: LocalDateTime,
  val numberOfAlbums: Int,
  val notFound: List<SyncAlbum>,
  val error: String?,
  val running: Boolean
)

@Serializable
data class SyncAlbum(
  val name: String,
  val artist: String
)