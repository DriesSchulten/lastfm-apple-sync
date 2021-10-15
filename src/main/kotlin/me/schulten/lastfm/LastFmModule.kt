package me.schulten.lastfm

import org.koin.dsl.module

val lastFmModule = module {
  single<LastFmClient> { LastFmClientImpl(get()) }
}