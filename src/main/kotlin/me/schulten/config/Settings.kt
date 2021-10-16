package me.schulten.config

import me.schulten.lastfm.Period

data class AppSettings(
  val lastFm: LastFm,
  val appleMusic: AppleMusic,
  val sync: Sync,
  val storage: Storage
)

data class LastFm(
  val baseUrl: String,
  val apiKey: String,
  val sharedSecret: String,
  val authUrl: String,
  val user: String,
  val topAlbumPeriod: Period
)

data class AppleMusic(
  val teamId: String,
  val keyId: String,
  val keyFile: String,
  val baseUrl: String
)

data class Sync(
  val minPlayCount: Int
)

data class Storage(
  val directory: String
)