package me.schulten.applemusic

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.schulten.config.AppSettings
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists

/**
 * Holds/upates Apple Music tokens
 */
interface AppleMusicCredentialHelper {
  /**
   * Current developer token
   */
  val developerToken: String

  /**
   * Current user token
   */
  val userToken: String

  /**
   * Store to use (based on user login)
   */
  val storefrontId: String

  /**
   * Set new user token
   */
  fun updateUserToken(userToken: UserToken)

  /**
   * Remove user token (disables connection)
   */
  fun deleteUserToken()
}

/**
 * Handles keeping track of Apple Music API credentials.
 *
 * @author dries
 */
class AppleMusicCredentialHelperImpl(appSettings: AppSettings, private val tokenGenerator: AppleMusicTokenGenerator) : AppleMusicCredentialHelper {

  private var tokens: AppleMusicTokens? = null

  private val storageFile = Paths.get(appSettings.storage.directory, STORED_NAME)

  init {
    if (storageFile.exists()) {
      val data = Files.readString(storageFile)
      tokens = Json.decodeFromString<AppleMusicTokens>(data)
    }
  }

  private fun storeTokens() {
    tokens?.let { data ->
      val json = Json.encodeToString(data)

      if (storageFile.notExists()) {
        storageFile.createFile()
      }

      Files.writeString(storageFile, json, StandardOpenOption.TRUNCATE_EXISTING)
    }
  }

  override val developerToken: String
    get() {
      val currentToken = tokens

      return if (currentToken != null && currentToken.developerTokenExpires.isAfter(LocalDateTime.now())) {
        currentToken.developerToken
      } else {
        // Either non-existing or expired
        val developerToken = tokenGenerator.generateToken()

        tokens = if (currentToken == null) {
          AppleMusicTokens(developerToken.token, developerToken.expires)
        } else {
          currentToken.copy(developerToken = developerToken.token, developerTokenExpires = developerToken.expires)
        }
        storeTokens()

        developerToken.token
      }
    }

  override val userToken: String
    get() {
      val currentToken = tokens

      if (currentToken?.userToken != null && currentToken.userTokenExpires?.isBefore(LocalDateTime.now()) == true) {
        throw ExpiredUserCredentialsException
      } else if (currentToken?.userToken == null) {
        throw MissingUserCredentialsException
      }

      return currentToken.userToken
    }

  override val storefrontId: String
    get() = tokens?.storefrontId ?: throw MissingUserCredentialsException

  override fun updateUserToken(userToken: UserToken) {
    val currentToken = tokens ?: throw MissingDeveloperCredentialsException

    // According to online sources a user token is valid for 6 months
    val expires = LocalDateTime.now().plusMonths(6).minusDays(1)

    tokens = currentToken.copy(userToken = userToken.token, userTokenExpires = expires, storefrontId = userToken.storefrontId)
    storeTokens()
  }

  override fun deleteUserToken() {
    val currentToken = tokens
    if (currentToken != null) {
      tokens = AppleMusicTokens(developerToken = currentToken.developerToken, developerTokenExpires = currentToken.developerTokenExpires)
      storeTokens()
    }
  }

  companion object {
    private const val STORED_NAME = "apple-music-tokens.json"
  }
}