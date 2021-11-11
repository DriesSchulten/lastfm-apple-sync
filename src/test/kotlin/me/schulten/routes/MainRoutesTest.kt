package me.schulten.routes

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.mockk.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.schulten.applemusic.AppleMusicCredentialHelper
import me.schulten.sync.SyncJobScheduler
import me.schulten.sync.SyncService
import me.schulten.sync.SyncStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.module.Module
import org.koin.dsl.module
import java.time.LocalDateTime

/**
 * Test the basic routes
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainRoutesTest : AbstractRoutesTest() {

  private val credentialHelper = mockk<AppleMusicCredentialHelper>()
  private val syncService = mockk<SyncService>()
  private val syncJobScheduler = mockk<SyncJobScheduler>()

  override val koinModule: Module = module {
    single { credentialHelper }
    single { syncService }
    single { syncJobScheduler }
  }

  override val registerRoutes: Application.() -> Unit = { registerMainRoutes() }

  @BeforeEach
  fun clearMocks() {
    clearMocks(credentialHelper, syncService, syncJobScheduler)
  }

  @Test
  fun `static should serve frontend assets`() = withSyncTestApplication {
    val call = handleRequest(HttpMethod.Get, "/")
    with(call) {
      assertEquals(HttpStatusCode.OK, response.status())
    }
  }

  @Test
  fun `sync should start the sync`() = withSyncTestApplication {
    every { syncService.currentSync } returns null
    justRun { syncJobScheduler.scheduleImmediately() }

    val call = handleRequest(HttpMethod.Get, "/sync")
    with(call) {
      assertEquals(HttpStatusCode.NoContent, response.status())
      verify { syncJobScheduler.scheduleImmediately() }
    }
  }

  @Test
  fun `sync should give a bad request if a sync is already running currently`() = withSyncTestApplication {
    every { syncService.currentSync } returns SyncStatus(LocalDateTime.now(), 0, emptyList(), null, true)
    justRun { syncJobScheduler.scheduleImmediately() }

    val call = handleRequest(HttpMethod.Get, "/sync")
    with(call) {
      assertEquals(HttpStatusCode.BadRequest, response.status())
      verify(exactly = 0) { syncJobScheduler.scheduleImmediately() }
    }
  }

  @Test
  fun `sync status should give current or last run sync status as response`() = withSyncTestApplication {
    val expectedStatus = SyncStatus(LocalDateTime.now(), 0, emptyList(), null, true)
    every { syncService.currentSync } returns expectedStatus

    val call = handleRequest(HttpMethod.Get, "/sync-status")
    with(call) {
      assertEquals(HttpStatusCode.OK, response.status())

      val result = Json.decodeFromString<SyncStatus>(response.content!!)
      assertEquals(expectedStatus, result)
    }
  }

  @Test
  fun `sync status should give a 404 when no sync has run or is running`() = withSyncTestApplication {
    every { syncService.currentSync } returns null

    val call = handleRequest(HttpMethod.Get, "/sync-status")
    with(call) {
      assertEquals(HttpStatusCode.NotFound, response.status())
    }
  }
}