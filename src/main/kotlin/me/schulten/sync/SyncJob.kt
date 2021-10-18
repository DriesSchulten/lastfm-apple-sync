package me.schulten.sync

import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.quartz.Job
import org.quartz.JobExecutionContext

private val logger = KotlinLogging.logger {}

/**
 * Quartz job to run the sync
 *
 * @author dries
 */
class SyncJob: Job, KoinComponent {

  private val syncService by inject<SyncService>()

  override fun execute(context: JobExecutionContext?) {
    logger.info { "Starting sync job" }
  }
}