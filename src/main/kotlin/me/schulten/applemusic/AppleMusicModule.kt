package me.schulten.applemusic

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val appleMusicModule = module {
  single { CIO.create() }

  single { AppleMusicTokenGenerator(get()) }
  single<AppleMusicCredentialHelper> { AppleMusicCredentialHelperImpl(get(), get()) }
  single<AppleMusicClient> { AppleMusicClientImpl(get(), get(), get()) }
  single<AppleMusicService> { AppleMusicServiceImpl(get()) }
}