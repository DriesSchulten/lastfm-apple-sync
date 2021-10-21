package me.schulten.applemusic

import me.schulten.config.*
import me.schulten.lastfm.Period
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.NoSuchFileException
import java.time.LocalDateTime

/**
 * Token generator tests
 *
 * @author dries
 */
class AppleMusicTokenGeneratorTest {

  private val testKeyPath: String by lazy {
    javaClass.getResource("/TestKey.p8")!!.toURI().path
  }

  private fun appSettings(keyPath: String, team: String = "CapExdTeam") = AppSettings(
    LastFm("", "", "", "", "", Period.WEEK),
    AppleMusic(team, "CapExedKid", keyPath, ""),
    Sync(1, ""),
    Storage("")
  )

  @Test
  fun `generate token should provide a developer token`() {
    val token = AppleMusicTokenGenerator(appSettings(testKeyPath)).generateToken()
    assertTrue(token.expires.isAfter(LocalDateTime.now().plusMonths(6).minusDays(2)))
  }

  @Test
  fun `generate token should throw an exception when the key file is missing`() {
    assertThrows<NoSuchFileException> {
      AppleMusicTokenGenerator(appSettings("not-a-path-to-a-key")).generateToken()
    }
  }
}