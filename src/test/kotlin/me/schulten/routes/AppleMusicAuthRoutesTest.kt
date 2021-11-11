package me.schulten.routes

import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.clearMocks
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.schulten.applemusic.AppleMusicCredentialHelper
import me.schulten.applemusic.UserToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Test the Apple Music auth routes
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppleMusicAuthRoutesTest : AbstractRoutesTest() {

  private val credentialHelper = mockk<AppleMusicCredentialHelper>()

  override val koinModule: Module = module {
    single { credentialHelper }
  }

  override val registerRoutes: Application.() -> Unit = { registerAppleMusicAuthRoutes() }

  @BeforeEach
  fun clearMocks() {
    clearMocks(credentialHelper)
  }

  @Test
  fun `register user tokens should store the tokens`() = withSyncTestApplication {
    justRun { credentialHelper.updateUserToken(any()) }

    val userToken = UserToken("my-user-token", "en")
    val call = handleRequest(HttpMethod.Post, "/apple-music/user-token") {
      addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
      setBody(Json.encodeToString(userToken))
    }
    with(call) {
      assertEquals(HttpStatusCode.NoContent, response.status())

      verify { credentialHelper.updateUserToken(eq(userToken)) }
    }
  }

  @Test
  fun `delete user tokens should call the helper to delete the token`() = withSyncTestApplication {
    justRun { credentialHelper.deleteUserToken() }

    val call = handleRequest(HttpMethod.Delete, "/apple-music/user-token")
    with(call) {
      assertEquals(HttpStatusCode.NoContent, response.status())

      verify { credentialHelper.deleteUserToken() }
    }
  }
}