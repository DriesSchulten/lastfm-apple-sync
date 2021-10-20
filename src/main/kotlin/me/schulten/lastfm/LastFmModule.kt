package me.schulten.lastfm

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val lastFmModule = module {
  single { CIO.create() }

  single<LastFmClient> { LastFmClientImpl(get(), get()) }
  single<LastFmService> { LastFmServiceImpl(get(), get()) }
}