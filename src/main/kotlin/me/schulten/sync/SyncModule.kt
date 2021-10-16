package me.schulten.sync

import org.koin.dsl.module

val syncModule = module {
  single<SyncService> { SyncServiceImpl(get(), get(), get()) }
}