package me.schulten.applemusic

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.ECDSAKeyProvider
import me.schulten.config.AppSettings
import me.schulten.config.AppleMusic
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.Date


/**
 * Generates Apple Music Developer JWT tokens
 *
 * @author dries
 */
class AppleMusicTokenGenerator(private val appSettings: AppSettings) {

  /**
   * Generates Apple Music API developer token
   * @return The developer token, to be used as bearer token
   * @throws com.auth0.jwt.exceptions.JWTCreationException
   */
  fun generateToken(): String {
    val algorithm = Algorithm.ECDSA256(P8KeyProvider(appSettings.appleMusic))

    val issuedAt = LocalDateTime.now()
    val expiresAt = issuedAt.plusHours(12)

    return JWT.create()
      .withIssuer(appSettings.appleMusic.teamId)
      .withIssuedAt(Date.from(issuedAt.toInstant(ZoneOffset.UTC)))
      .withExpiresAt(Date.from(expiresAt.toInstant(ZoneOffset.UTC)))
      .sign(algorithm)!!
  }

  private class P8KeyProvider(private val appleMusic: AppleMusic) : ECDSAKeyProvider {

    private val pk: ECPrivateKey by lazy {
      val lines = Files.readAllLines(Paths.get(appleMusic.keyFile))
      val encodedKey = Base64.getDecoder().decode(lines.filterNot { it.startsWith("-----") }.joinToString(""))

      val factory = KeyFactory.getInstance("EC")
      factory.generatePrivate(PKCS8EncodedKeySpec(encodedKey)) as ECPrivateKey
    }

    override fun getPublicKeyById(keyId: String?): ECPublicKey =
      throw NotImplementedError("No public key available for verification")

    override fun getPrivateKey(): ECPrivateKey = pk

    override fun getPrivateKeyId(): String = appleMusic.keyId
  }
}