package io.clopen.epsilon.jwt

import JwtService
import io.clopen.epsilon.keys.EpsilonKey
import io.clopen.epsilon.keys.KeyManagementService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.KeyException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Optional
import java.util.Date

@Service
class JwtServiceImpl(
    private val keyManagementService: KeyManagementService
) : JwtService {

    override fun generateToken(
        userId: String,
        expirationTime: Instant,
        applicationName: String,
        companyName: String
    ): String {
        val epsilonKey = getCurrentKey()
        val claims: MutableMap<String, Any> = mutableMapOf()

        claims["application"] = applicationName
        claims["company"] = companyName

        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expirationTime))
            .addClaims(claims)
            .signWith(epsilonKey.key, getSignatureAlgorithm(epsilonKey))
            .compact()
    }

    override fun validateToken(token: String): Boolean {
        return try {
            val secretKey = getCurrentKey()
            Jwts.parserBuilder()
                .setSigningKey(secretKey.key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun extractUserId(token: String): Optional<String> {
        return try {
            val epsilonKey = getCurrentKey()
            val claims = Jwts.parserBuilder()
                .setSigningKey(epsilonKey.key)
                .build()
                .parseClaimsJws(token)
                .body
            Optional.ofNullable(claims.subject)
        } catch (e: Exception) {
            Optional.empty()
        }
    }

    private fun getCurrentKey(): EpsilonKey {
        return keyManagementService
            .getCurrentKey()
            .orElseThrow { KeyException("Signing key could not be found") }
    }

    private fun getSignatureAlgorithm(epsilonKey: EpsilonKey): SignatureAlgorithm {
        return when (epsilonKey.algorithm.uppercase()) {
            "HS256", "HMACSHA256" -> SignatureAlgorithm.HS256
            "HS384" -> SignatureAlgorithm.HS384
            "HS512" -> SignatureAlgorithm.HS512
            "RS256" -> SignatureAlgorithm.RS256
            "RS384" -> SignatureAlgorithm.RS384
            "RS512" -> SignatureAlgorithm.RS512
            else -> throw IllegalArgumentException("Unsupported algorithm: ${epsilonKey.algorithm}")
        }
    }
}
