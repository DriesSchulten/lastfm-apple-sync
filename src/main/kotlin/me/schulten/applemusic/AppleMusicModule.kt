package me.schulten.applemusic

import org.koin.dsl.module

val appleMusicModule = module {
  single { AppleMusicTokenGenerator(get()) }
}