package me.schulten.sync

import me.schulten.config.AppSettings
import org.koin.dsl.module
import org.quartz.impl.StdSchedulerFactory

val syncModule = module {
  single<SyncService> { SyncServiceImpl(get(), get(), get()) }

  single {
    val schedulerFactory = StdSchedulerFactory()
    val scheduler = schedulerFactory.scheduler

    scheduler
  }

  single<SyncJobScheduler> { SyncJobSchedulerImpl(get(), get()) }
}