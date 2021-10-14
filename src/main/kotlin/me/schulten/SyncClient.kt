package me.schulten

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.sksamuel.hoplite.ConfigLoader
import kotlinx.coroutines.runBlocking
import me.schulten.config.AppSettings
import me.schulten.lastfm.LastFmClient

class SyncClient : CliktCommand() {
  private val user by option(help = "LastFM user name").required()
  //private val period by option(help = "Period to fetch albums for")

  override fun run() = runBlocking {
    val appSettings = ConfigLoader().loadConfigOrThrow<AppSettings>("/application.conf")
    val lastFmClient = LastFmClient(appSettings)

    val albums = lastFmClient.getTopAlbums(user)
    echo("Found ${albums.size} albums for user $user")
  }

}

fun main(args: Array<String>) = SyncClient().main(args)
