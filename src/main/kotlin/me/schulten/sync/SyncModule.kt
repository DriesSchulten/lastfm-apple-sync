package me.schulten.sync

import me.schulten.config.AppSettings
import org.koin.dsl.module
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory

val syncModule = module {
  single<SyncService> { SyncServiceImpl(get(), get(), get()) }

  single {
    val appSettings = get<AppSettings>()

    val schedulerFactory = StdSchedulerFactory()
    val scheduler = schedulerFactory.scheduler

    val syncJob = newJob(SyncJob::class.java)
      .withIdentity("weekly-sync", "sync")
      .build()

    val trigger = newTrigger()
      .withIdentity("sync-trigger", "sync")
      .withSchedule(cronSchedule(appSettings.sync.cronSchedule))
      .build()

    scheduler.scheduleJob(syncJob, trigger)

    scheduler
  }
}