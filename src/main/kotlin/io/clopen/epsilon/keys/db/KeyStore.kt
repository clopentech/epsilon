package io.clopen.epsilon.keys.db

import io.clopen.epsilon.keys.KeyManagementConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.Optional
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Component
class KeyStore @Autowired constructor(
    private val keyRepository: KeyRepository,
    private val keyManagementConfig: KeyManagementConfig
) {

    fun generateKey(alias: String): KeyInformation {
        val algorithm = keyManagementConfig.algorithm
        val key = generateSecretKey(algorithm)
        return storeKey(alias, key, algorithm)
    }

    fun storeKey(alias: String, key: SecretKey, algorithm: String): KeyInformation {
        val keyString = Base64.getEncoder().encodeToString(key.encoded)
        val keyInfo = KeyInformation(
            alias = alias,
            algorithm = algorithm,
            encryptionKey = keyString,
            createdAt = Instant.now(),
            expiryDate = Instant.now().plus(90, ChronoUnit.DAYS)
        )
        return keyRepository.save(keyInfo)
    }


    fun getKey(alias: String): Optional<KeyInformation> {
        return keyRepository.findByAlias(alias)
    }

    fun getCurrentKey(): Optional<KeyInformation> {
        return keyRepository.findFirstByOrderByExpiryDateDesc()
    }

    private fun generateSecretKey(algorithm: String): SecretKey {
        return try {
            val keyGenerator = KeyGenerator.getInstance(algorithm)
            keyGenerator.init(256)
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("$algorithm algorithm not available", e)
        }
    }

}