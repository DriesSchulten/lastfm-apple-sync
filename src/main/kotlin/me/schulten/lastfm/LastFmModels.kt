package me.schulten.lastfm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Period(val argName: String) {
  OVWRALL("overall"), WEEK("7day"), MONTH("1month"), QUARTER_YEAR("3month"), HALF_YEAR("6month"), YEAR("12month")
}

@Serializable
data class TopAlbumsResult(@SerialName("topalbums") val topAlbums: TopAlbums)

@Serializable
data class TopAlbums(
  @SerialName("album") val albums: List<Album>,
  @SerialName("@attr") val attributes: ResultAttributes
)

@Serializable
data class ResultAttributes(val totalPages: Int, val page: Int, val perPage: Int, val total: Int)

@Serializable
data class Album(val mbid: String, val name: String, val playcount: Int, val artist: Artist)

@Serializable
data class Artist(val mbid: String, val name: String)