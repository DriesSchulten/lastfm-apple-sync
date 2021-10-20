package me.schulten.applemusic

import me.schulten.config.*
import me.schulten.lastfm.Period
import org.junit.Assert
import org.junit.Test
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
  fun generateTokenTest() {
    val token = AppleMusicTokenGenerator(appSettings(testKeyPath)).generateToken()
    Assert.assertTrue(token.expires.isAfter(LocalDateTime.now().plusMonths(6).minusDays(2)))
  }

  @Test(expected = NoSuchFileException::class)
  fun generateTokenMissingKeyFileTest() {
    AppleMusicTokenGenerator(appSettings("not-a-path-to-a-key")).generateToken()
  }
}