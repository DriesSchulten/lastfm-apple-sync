package me.schulten.applemusic

import org.koin.dsl.module

val appleMusicModule = module {
  single { AppleMusicTokenGenerator(get()) }
  single { AppleMusicCredentialHelper(get(), get()) }
  single<AppleMusicClient> { AppleMusicClientImpl(get(), get()) }
  single<AppleMusicService> { AppleMusicServiceImpl(get()) }
}