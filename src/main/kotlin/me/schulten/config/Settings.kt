package me.schulten.config

data class AppSettings(
  val lastFm: LastFm
)

data class LastFm(
  val baseUrl: String,
  val apiKey: String,
  val sharedSecret: String,
  val authUrl: String
)