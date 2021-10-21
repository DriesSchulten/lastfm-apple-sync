package me.schulten.applemusic

import io.mockk.every
import io.mockk.mockk
import me.schulten.config.*
import me.schulten.lastfm.Period
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists

/**
 * Credential helper tests
 *
 * @author dries
 */
class AppleMusicCredentialHelperTest {

  private fun settings(storageDirectory: String): AppSettings = AppSettings(
    LastFm("", "", "", "", "", Period.WEEK),
    AppleMusic("", "", "", ""),
    Sync(1, ""),
    Storage(storageDirectory)
  )

  @Test
  fun `the developer token should be returned if present and not expired`() = run("/apple-tokens-valid.json") { helper ->
    assertEquals("developer-token", helper.developerToken)
  }

  @Test
  fun `the developer token should be updated when it's expired`() {
    val updatedToken = "updated-token"

    val generator = mockk<AppleMusicTokenGenerator>()
    every { generator.generateToken() } returns DeveloperToken(updatedToken, LocalDateTime.now().plusDays(30))

    run(tokenFile = "/apple-tokens-expired.json", generator = generator) { helper ->
      assertEquals(updatedToken, helper.developerToken)
    }
  }

  @Test
  fun `the developer token should be generated when it's null`() {
    val newToken = "newly-generated-token"

    val generator = mockk<AppleMusicTokenGenerator>()
    every { generator.generateToken() } returns DeveloperToken(newToken, LocalDateTime.now().plusDays(30))

    run(generator = generator) { helper ->
      assertEquals(newToken, helper.developerToken)
    }
  }

  @Test
  fun `the user token should be returned if present and valid`() = run("/apple-tokens-valid.json") { helper ->
    assertEquals("user-token", helper.userToken)
  }

  @Test
  fun `an exception should be thrown when the user token is expired`() = run("/apple-tokens-expired.json") { helper ->
    assertThrows<ExpiredUserCredentialsException> {
      helper.userToken
    }
  }

  @Test
  fun `an exception should be thrown when the user token is null`() = run { helper ->
    assertThrows<MissingUserCredentialsException> {
      helper.userToken
    }
  }

  @Test
  fun `the storefront id should be returned when present`() = run("/apple-tokens-valid.json") { helper ->
    assertEquals("nl", helper.storefrontId)
  }

  @Test
  fun `the new user tokens should be updated and subsequently returned`() = run("/apple-tokens-valid.json") { helper ->
    val newToken = UserToken("abc", "en")
    helper.updateUserToken(newToken)

    assertEquals(newToken.token, helper.userToken)
    assertEquals(newToken.storefrontId, helper.storefrontId)
  }

  @Test
  fun `the user tokens should be deleted`() = run("/apple-tokens-valid.json") { helper ->
    helper.deleteUserToken()
    assertThrows<MissingUserCredentialsException> {
      helper.userToken
    }
  }

  private fun run(
    tokenFile: String? = null,
    generator: AppleMusicTokenGenerator = mockk(),
    block: (AppleMusicCredentialHelper) -> Unit
  ) {
    val dir = if (tokenFile == null) {
      Files.createTempDirectory("credential-test").absolutePathString()
    } else {
      copyTokensFile(tokenFile)
    }

    try {
      val settings = settings(dir)

      val helper = AppleMusicCredentialHelperImpl(settings, generator)
      block(helper)
    } finally {
      cleanUp(dir)
    }

  }

  private fun copyTokensFile(file: String): String {
    val tempDirectory = Files.createTempDirectory("credential-test")
    Files.copy(javaClass.getResourceAsStream(file)!!, tempDirectory.resolve("apple-music-tokens.json"))

    return tempDirectory.absolutePathString()
  }

  private fun cleanUp(tempDir: String) {
    val dir = Path(tempDir)
    dir.resolve("apple-music-tokens.json").deleteIfExists()
    dir.deleteIfExists()
  }
}