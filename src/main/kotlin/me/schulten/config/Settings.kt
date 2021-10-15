package me.schulten.config

data class AppSettings(
  val lastFm: LastFm,
  val appleMusic: AppleMusic,
  val storage: Storage
)

data class LastFm(
  val baseUrl: String,
  val apiKey: String,
  val sharedSecret: String,
  val authUrl: String
)

data class AppleMusic(
  val teamId: String,
  val keyId: String,
  val keyFile: String
)

data class Storage(
  val directory: String
)