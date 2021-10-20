package me.schulten.applemusic

import io.mockk.every
import io.mockk.mockk
import me.schulten.config.*
import me.schulten.lastfm.Period
import org.junit.Assert
import org.junit.Test
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
  fun developerTokenValidTest() = run("/apple-tokens-valid.json") { helper ->
    Assert.assertEquals("developer-token", helper.developerToken)
  }

  @Test
  fun developerTokenExpiredTest() {
    val updatedToken = "updated-token"

    val generator = mockk<AppleMusicTokenGenerator>()
    every { generator.generateToken() } returns DeveloperToken(updatedToken, LocalDateTime.now().plusDays(30))

    run(tokenFile = "/apple-tokens-expired.json", generator = generator) { helper ->
      Assert.assertEquals(updatedToken, helper.developerToken)
    }
  }

  @Test
  fun developerTokenGeneratesIfAbsentTest() {
    val newToken = "newly-generated-token"

    val generator = mockk<AppleMusicTokenGenerator>()
    every { generator.generateToken() } returns DeveloperToken(newToken, LocalDateTime.now().plusDays(30))

    run(generator = generator) { helper ->
      Assert.assertEquals(newToken, helper.developerToken)
    }
  }

  @Test
  fun userTokenValidTest() = run("/apple-tokens-valid.json") { helper ->
    Assert.assertEquals("user-token", helper.userToken)
  }

  @Test(expected = ExpiredUserCredentialsException::class)
  fun userTokenExpiredTest() = run("/apple-tokens-expired.json") { helper ->
    helper.userToken
  }

  @Test(expected = MissingUserCredentialsException::class)
  fun userTokenIsAbsentTest() = run { helper ->
    helper.userToken
  }

  @Test
  fun storeFrontIdTest() = run("/apple-tokens-valid.json") { helper ->
    Assert.assertEquals("nl", helper.storefrontId)
  }

  @Test
  fun updateUserTokenTest() = run("/apple-tokens-valid.json") { helper ->
    val newToken = UserToken("abc", "en")
    helper.updateUserToken(newToken)

    Assert.assertEquals(newToken.token, helper.userToken)
    Assert.assertEquals(newToken.storefrontId, helper.storefrontId)
  }

  @Test(expected = MissingUserCredentialsException::class)
  fun deleteUserTokenTest() = run("/apple-tokens-valid.json") { helper ->
    helper.deleteUserToken()
    helper.userToken
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