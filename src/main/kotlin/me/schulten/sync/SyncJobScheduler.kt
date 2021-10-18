package me.schulten.sync

import me.schulten.config.AppSettings
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder

/**
 * Setups up scheduling for the sync
 *
 * @author dries
 */
interface SyncJobScheduler {

  /**
   * Schedule a sync right now, for manual triggering
   */
  fun scheduleImmediately()
}

class SyncJobSchedulerImpl(private val appSettings: AppSettings, private val scheduler: Scheduler) : SyncJobScheduler {

  init {
    val syncJob = JobBuilder.newJob(SyncJob::class.java)
      .withIdentity("weekly-sync", "sync")
      .build()

    val trigger = TriggerBuilder.newTrigger()
      .withIdentity("sync-trigger", "sync")
      .withSchedule(CronScheduleBuilder.cronSchedule(appSettings.sync.cronSchedule))
      .build()

    scheduler.scheduleJob(syncJob, trigger)
  }

  override fun scheduleImmediately() {
    val syncJob = JobBuilder.newJob(SyncJob::class.java)
      .withIdentity("sync-now", "sync")
      .build()

    val trigger = TriggerBuilder.newTrigger()
      .withIdentity("sync-trigger-now", "sync")
      .startNow()
      .build()

    scheduler.scheduleJob(syncJob, trigger)
  }
}
